package com.almasb.fxhub;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.texture.ColoredTexture;
import com.almasb.fxgl.ui.FXGLScrollPane;
import com.almasb.fxgl.ui.FontType;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FXHubApp extends GameApplication {

    private ObjectProperty<MenuItem> selectedMenuItem;

    private List<ProjectInfo> apps;

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
        getNetService().openStreamTask("https://raw.githubusercontent.com/AlmasB/FXHub-data/main/apps.txt")
                .onSuccess(stream -> {
                    try (stream) {
                        apps = new ArrayList<>();

                        var file = Paths.get("apps.txt");

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

                                apps.add(project);

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

                        apps.add(project);


                        System.out.println(apps);

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

        var title = new Title(getSettings().getTitle());

        addUINode(title);


        //

        Text selectedItemTitle = getUIFactoryService().newText("Apps", Color.BLACK, 25.0);

        //addUINode(selectedItemTitle, 280, 80);


        selectedMenuItem = new SimpleObjectProperty<>();
        selectedMenuItem.set(new MenuItem("Apps", selectedMenuItem, () -> {}));
        selectedMenuItem.get().setSelected(true);
        selectedMenuItem.addListener((o, oldValue, newValue) -> {
            oldValue.setSelected(false);
            newValue.setSelected(true);

            selectedItemTitle.setText(newValue.text.getText());
        });

        VBox box = new VBox(
                selectedMenuItem.get(),
                new MenuItem("Games", selectedMenuItem, () -> {}),
                new MenuItem("Projects", selectedMenuItem, () -> {}),
                new MenuItem("Tutorials", selectedMenuItem, () -> {}),
                new MenuItem("Search", selectedMenuItem, () -> {})
        );

        addUINode(box, 0, 50);

        // projects
        var vbox = new VBox();
        vbox.setPadding(new Insets(5));

        apps.forEach(project -> {
            var view = new ProjectView(project);

            vbox.getChildren().addAll(view);
        });

        var scrollPane = new FXGLScrollPane(vbox);
        scrollPane.setPrefWidth(780);
        scrollPane.setPrefHeight(getAppHeight() - 110);

        addUINode(scrollPane, 265, 60);
    }

    private static class Title extends StackPane {
        Title(String name) {
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

        Text text;

        MenuItem(String name, ObjectProperty<MenuItem> selector, Runnable action) {
            text = new Text(name);
            Rectangle bg0 = new Rectangle(250, 60);

            bg0.fillProperty().bind(
                    Bindings.when(hoverProperty())
                            .then(Color.color(0.5, 0.5, 0.5, 0.5)).otherwise(Color.TRANSPARENT)
            );

            text.setFont(Font.font(22.0));

            setOnMouseClicked(e -> {
                selector.set(this);
                action.run();
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
            var image = new Image(
                    project.getScreenshotLink(),
                    bg.getWidth() - 50,
                    500,
                    true,
                    true,
                    true
            );

            content = new ImageView();
            content.imageProperty().bind(
                    Bindings.when(image.progressProperty().lessThan(1.0))
                            .then(new ColoredTexture((int) bg.getWidth() - 50, 500, Color.AQUAMARINE).getImage())
                            .otherwise(image)
            );
            content.setTranslateX(25);
            content.setTranslateY(bg.getHeight());

            bg.setArcWidth(2.5);
            bg.setArcHeight(2.5);

            var btn = getUIFactoryService().newButton("Download");
            btn.setPrefWidth(80);
            btn.fontProperty().unbind();
            btn.setFont(Font.font(18));
            btn.setOnAction(e -> {

                var fileNameNoExt = project.getTitle().replace(' ', '-') + "-" + project.getVersion();

                // TODO: move download + progress to FXGL codebase
                // TODO: Windows hardcoded
                var task = getNetService().openStreamTask(project.getExeZipLinkWindows())
                        .thenWrap(stream -> {
                            try (stream) {
                                var file = Paths.get(fileNameNoExt + ".zip");
                                Files.copy(stream, file);

                                return file;
                            } catch (Exception ex) {
                                ex.printStackTrace();

                                throw new RuntimeException("Cannot download: " + project.getExeZipLinkWindows());
                            }
                        })
                        .thenWrap(zippedFile -> {
                            Unzipper.unzip(zippedFile.toFile(), Paths.get(fileNameNoExt).toFile());
                            return "";
                        })
                        .onSuccess(nothing -> {
                            System.out.println("Success");
                        })
                        .onFailure(ex -> ex.printStackTrace());

                getTaskService().runAsyncFX(task);

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
                showMessage("Not implemented!");
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

            getChildren().addAll(bg, vbox, btn);

            bg.setEffect(new DropShadow(5, Color.BLACK));

            setOnMouseClicked(e -> {
                if (isCollapsed) {
                    getChildren().remove(content);
                } else {
                    getChildren().add(0, content);
                }

                isCollapsed = !isCollapsed;
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
