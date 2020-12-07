package com.almasb.fxhub;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.texture.ColoredTexture;
import com.almasb.fxgl.ui.FXGLScrollPane;
import com.almasb.fxgl.ui.FontType;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.almasb.fxgl.dsl.FXGL.*;

// TODO: self-update

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FXHubApp extends GameApplication {

    private static final String APPS_LINK = "https://raw.githubusercontent.com/AlmasB/FXHub-data/main/apps.txt";
    private static final String GAMES_LINK = "https://raw.githubusercontent.com/AlmasB/FXHub-data/main/games.txt";
    private static final String PROJECTS_LINK = "https://raw.githubusercontent.com/AlmasB/FXHub-data/main/projects.txt";

    private ObjectProperty<MenuItem> selectedMenuItem;

    private List<ProjectInfo> apps;
    private List<ProjectInfo> games;
    private List<ProjectInfo> projects;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXHub");
        settings.setVersion("0.2-SNAPSHOT");
        settings.setWidth(1066);
        settings.setHeightFromRatio(16/9.0);
        settings.getCSSList().add("fxstore.css");
    }

    @Override
    protected void initGame() {
        apps = new ArrayList<>();
        games = new ArrayList<>();
        projects = new ArrayList<>();

        Map<String, List<ProjectInfo>> cache = Map.of(
                APPS_LINK, apps,
                GAMES_LINK, games,
                PROJECTS_LINK, projects
        );

        cache.forEach((link, list) -> {
            loadProjectsInto(link, link.substring(link.lastIndexOf("/") + 1), list);

            list.sort(Comparator.comparing(ProjectInfo::getTitle));
        });
    }

    private void loadProjectsInto(String link, String cacheFileName, List<ProjectInfo> projects) {
        getNetService().openStreamTask(link)
                .onSuccess(stream -> {
                    try (stream) {

                        // TODO: clean up parsing

                        var file = Paths.get(cacheFileName);

                        Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);

                        var lines = Files.readAllLines(file);

                        String title = "Untitled";
                        String version = "0.0";
                        String description = "No description";
                        List<String> authors = new ArrayList<>();
                        List<String> tags = new ArrayList<>();
                        String website = "";
                        String screenshotLink = "";
                        String exeZipLinkWindows = "";
                        String exeZipLinkLinux = "";
                        String exeZipLinkMac = "";

                        for (var line : lines) {
                            var trimmedLine = line.trim();

                            // new project
                            if (trimmedLine.startsWith("---")) {

                                var project = new ProjectInfo(
                                        title,
                                        version,
                                        description,
                                        authors,
                                        tags,
                                        website,
                                        screenshotLink,
                                        exeZipLinkWindows,
                                        exeZipLinkLinux,
                                        exeZipLinkMac
                                );

                                projects.add(project);

                                title = "Untitled";
                                version = "0.0";
                                description = "No description";
                                authors = new ArrayList<>();
                                tags = new ArrayList<>();
                                website = "";
                                screenshotLink = "";
                                exeZipLinkWindows = "";
                                exeZipLinkLinux = "";
                                exeZipLinkMac = "";

                                continue;
                            }

                            if (trimmedLine.startsWith("#") || trimmedLine.isEmpty()) {
                                continue;
                            }

                            int indexOfEquals = trimmedLine.indexOf('=');

                            if (indexOfEquals == -1) {
                                continue;
                            }


                            // TODO: indexOfEquals + 1 vs length check?
                            var key = trimmedLine.substring(0, indexOfEquals).trim();
                            var value = trimmedLine.substring(indexOfEquals + 1).trim();

                            if (key.isEmpty() || value.isEmpty()) {
                                continue;
                            }

                            if (key.equals("title")) {
                                title = value;
                            } else if (key.equals("version")) {
                                version = value;
                            } else if (key.equals("description")) {
                                description = value;
                            } else if (key.equals("authors")) {
                                authors = Arrays.asList(value.split(","));
                            } else if (key.equals("tags")) {
                                tags = Arrays.asList(value.split(","));
                            } else if (key.equals("website")) {
                                website = value;
                            } else if (key.equals("screenshot")) {
                                screenshotLink = value;
                            } else if (key.equals("exeWindows")) {
                                exeZipLinkWindows = value;
                            } else if (key.equals("exeLinux")) {
                                exeZipLinkLinux = value;
                            } else if (key.equals("exeMac")) {
                                exeZipLinkMac = value;
                            } else {
                                System.out.println("Unknown key: " + key + " Value: " + value);
                            }
                        }

                        // left over
                        var project = new ProjectInfo(
                                title,
                                version,
                                description,
                                authors,
                                tags,
                                website,
                                screenshotLink,
                                exeZipLinkWindows,
                                exeZipLinkLinux,
                                exeZipLinkMac
                        );

                        projects.add(project);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .onFailure(e -> e.printStackTrace())
                .run();
    }

    @Override
    protected void initUI() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        // header
        addUINode(new Header(getSettings().getTitle()));

        // left side
        selectedMenuItem = new SimpleObjectProperty<>();

        // TODO: clean up repetition when we know what each menu item does...
        VBox sidePane = new VBox(
                new MenuItem("Apps", selectedMenuItem, new LazyValue<>(() -> {
                    var projectsPane = new VBox();
                    projectsPane.setPadding(new Insets(5));
                    apps.forEach(project -> {
                        projectsPane.getChildren().addAll(new ProjectView(project));
                    });

                    return projectsPane;
                })),

                new MenuItem("Games", selectedMenuItem, new LazyValue<>(() -> {
                    var projectsPane = new VBox();
                    projectsPane.setPadding(new Insets(5));
                    games.forEach(project -> {
                        projectsPane.getChildren().addAll(new ProjectView(project));
                    });

                    return projectsPane;
                })),

                new MenuItem("Projects", selectedMenuItem, new LazyValue<>(() -> {
                    var projectsPane = new VBox();
                    projectsPane.setPadding(new Insets(5));
                    projects.forEach(project -> {
                        projectsPane.getChildren().addAll(new ProjectView(project));
                    });

                    return projectsPane;
                })),

                new MenuItem("Tutorials", selectedMenuItem, new LazyValue<>(() -> {
                    return new Text("No tutorials");
                })),

                new MenuItem("Search", selectedMenuItem, new LazyValue<>(() -> {
                    return new Text("Not implemented yet");
                }))
        );

        addUINode(sidePane, 0, 50);

        // right side
        var scrollPane = new FXGLScrollPane();
        scrollPane.setPrefWidth(780);
        scrollPane.setPrefHeight(getAppHeight() - 110);

        addUINode(scrollPane, 265, 60);

        // selection logic

        selectedMenuItem.set((MenuItem) sidePane.getChildren().get(0));
        selectedMenuItem.get().setSelected(true);
        scrollPane.setContent(selectedMenuItem.get().content.get());

        selectedMenuItem.addListener((o, oldItem, newItem) -> {
            oldItem.setSelected(false);
            newItem.setSelected(true);

            scrollPane.setContent(newItem.content.get());
        });
    }

    private static class Header extends StackPane {
        Header(String name) {
            var text = getUIFactoryService().newText(name, Color.BLACK, 22.0);
            text.setTranslateX(15);

            var bg = new Rectangle(getAppWidth(), 40, Color.WHITE);

            setEffect(new DropShadow(5, Color.BLACK));

            setAlignment(Pos.CENTER_LEFT);

            getChildren().addAll(bg, text);
        }
    }

    private static class MenuItem extends StackPane {
        private Rectangle line = new Rectangle(5, 60, Color.ORANGE);

        private LazyValue<Node> content;

        private Text text;

        MenuItem(String name, ObjectProperty<MenuItem> selector, LazyValue<Node> content) {
            this.content = content;
            text = new Text(name);


            Rectangle bg0 = new Rectangle(250, 60);

            bg0.fillProperty().bind(
                    Bindings.when(hoverProperty())
                            .then(Color.color(0.5, 0.5, 0.5, 0.5)).otherwise(Color.TRANSPARENT)
            );

            text.setFont(Font.font(22.0));

            setOnMouseClicked(e -> {
                selector.set(this);
            });

            setAlignment(Pos.CENTER_LEFT);

            HBox box = new HBox(15, line, text);
            box.setPrefWidth(300);
            box.setAlignment(Pos.CENTER_LEFT);

            getChildren().addAll(bg0, box);

            setSelected(false);
        }

        void setSelected(boolean isSelected) {
            line.setVisible(isSelected);
            text.setFill(isSelected ? Color.BLACK : Color.color(0.0, 0.0, 0.0, 0.6));
        }
    }

    private static class ProjectView extends Pane {
        private Rectangle bg = new Rectangle(750, 110, Color.WHITE);

        private ImageView content;

        private boolean isCollapsed = false;

        ProjectView(ProjectInfo project) {
            content = new ImageView();

            // TODO: lazy loading of images
//            if (!project.getScreenshotLink().isEmpty()) {
//                var image = new Image(
//                        project.getScreenshotLink(),
//                        bg.getWidth() - 50,
//                        500,
//                        true,
//                        true,
//                        true
//                );
//
//                content.imageProperty().bind(
//                        Bindings.when(image.progressProperty().lessThan(1.0))
//                                .then(new ColoredTexture((int) bg.getWidth() - 50, 500, Color.AQUAMARINE).getImage())
//                                .otherwise(image)
//                );
//            } else {
//                content.setImage(new ColoredTexture((int) bg.getWidth() - 50, 500, Color.AQUAMARINE).getImage());
//            }

            content.setTranslateX(25);
            content.setTranslateY(bg.getHeight());

            bg.setArcWidth(2.5);
            bg.setArcHeight(2.5);

            var btn = getUIFactoryService().newButton("Download and Run");
            btn.setPrefWidth(110);
            btn.fontProperty().unbind();
            btn.setFont(Font.font(18));

            btn.setOnAction(e -> {


                var fileNameNoExt = project.getTitle().replace(' ', '-') + "-" + project.getVersion();

                // TODO: cancel download
                // TODO: if file already exists
                // TODO: move download + progress to FXGL codebase
                // TODO: Windows hardcoded
                var task = getNetService().openStreamTask(project.getExeZipLinkWindows())
                        .thenWrap(stream -> {
                            try (stream) {
                                var file = Paths.get(fileNameNoExt + ".zip");
                                Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);

                                return file;
                            } catch (Exception ex) {
                                ex.printStackTrace();

                                throw new RuntimeException("Cannot download: " + project.getExeZipLinkWindows());
                            }
                        })
                        .thenWrap(zippedFile -> {
                            var destinationDir = Paths.get(fileNameNoExt);

                            Unzipper.unzip(zippedFile.toFile(), destinationDir.toFile());
                            return destinationDir;
                        })
                        .thenWrap(destinationDir -> {
                            try {
                                Files.list(destinationDir.resolve("bin"))
                                        .filter(path -> path.toAbsolutePath().toString().endsWith(".bat"))
                                        .findAny()
                                        .ifPresent(batFile -> {
                                            System.out.println("Found bat file: " + batFile);

                                            AppRunner.run(batFile.toFile());
                                        });

                                return "";
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                                throw new RuntimeException("Failed to read bin/");
                            }
                        })
                        .onSuccess(destinationDir -> {
                            System.out.println("Success");
                        })
                        .onFailure(ex -> ex.printStackTrace());

                getTaskService().runAsyncFXWithDialog(task, "Downloading ~60MB, then running it. Please wait...");
            });
            btn.setCursor(Cursor.HAND);

            btn.setTranslateX(bg.getWidth() - btn.getPrefWidth() - 10);
            btn.setTranslateY(10);

            var projectNameText = new Text(project.getTitle());
            projectNameText.setFont(Font.font(24));

            var projectVersionText = getUIFactoryService().newText(project.getVersion(), Color.BLACK, 18);
            var authorNameText = getUIFactoryService().newText(project.getAuthors().toString(), Color.BLACK, 14.0);
            var projectWebsiteText = getUIFactoryService().newText(project.getWebsite(), Color.BLACK, FontType.MONO, 14.0);
            projectWebsiteText.setWrappingWidth(bg.getWidth() - 50);
            projectWebsiteText.setCursor(Cursor.HAND);
            projectWebsiteText.setOnMouseClicked(e -> {
                if (!project.getWebsite().isEmpty())
                    getFXApp().getHostServices().showDocument(project.getWebsite());
            });

            projectWebsiteText.fillProperty().bind(
                    Bindings.when(projectWebsiteText.hoverProperty())
                            .then(Color.BLUE)
                            .otherwise(Color.BLACK)
            );

            var box = new HBox(15, projectNameText, projectVersionText);
            //box.setTranslateX(10);
            //box.setTranslateY(5);

            var vbox = new VBox(10, box, authorNameText, projectWebsiteText);
            vbox.setPadding(new Insets(10));

            getChildren().addAll(bg, vbox);

            if (!project.getExeZipLinkWindows().isEmpty()) {
                getChildren().add(btn);
            }

            bg.setEffect(new DropShadow(5, Color.BLACK));

//            setOnMouseClicked(e -> {
//                if (isCollapsed) {
//                    getChildren().remove(content);
//                } else {
//                    getChildren().add(0, content);
//                }
//
//                isCollapsed = !isCollapsed;
//            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
