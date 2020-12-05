package com.almasb.fxstore;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.ColoredTexture;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FXGLScrollPane;
import com.almasb.fxgl.ui.FontType;
import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FXStoreApp extends GameApplication {

    private ObjectProperty<MenuItem> selectedMenuItem;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXStore");
        settings.setVersion("0.1-SNAPSHOT");
        settings.setWidth(1066);
        settings.setHeightFromRatio(16/9.0);
        settings.getCSSList().add("fxstore.css");
    }

    @Override
    protected void initUI() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        var title = new Title("FXStore");

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
                new MenuItem("Tutorials", selectedMenuItem, () -> {})
        );

        addUINode(box, 0, 50);



        var vbox = new VBox();
        vbox.getChildren().addAll(new ProjectView(
                "Almas Baimagambetov",
                "FXGL Breakout",
                "2.0",
                "https://github.com/AlmasB/FXGLGames/tree/master/Breakout"
        ), new Separator());


        for (int i = 0; i < 5; i++) {
            var project = new ProjectView(
                    "AuthorName",
                    "ProjectName",
                    "1.0." + i,
                    "https://github.com/AuthorName/ProjectName/..."
            );

            vbox.getChildren().addAll(project, new Separator());
        }

        vbox.setPadding(new Insets(5));

        var scrollPane = new FXGLScrollPane(vbox);
        scrollPane.setPrefWidth(780);
        scrollPane.setPrefHeight(getAppHeight() - 110);

        addUINode(scrollPane, 265, 60);


//        var image = new ColoredTexture(600, 400, Color.LIGHTSALMON);

//
//        var stack = new StackPane(new Rectangle(600, 400, Color.TRANSPARENT), btn);
//        stack.setAlignment(Pos.TOP_RIGHT);
//
//        addUINode(stack, 200, 50);
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

        private Texture content;

        private boolean isCollapsed = false;

        ProjectView(String authorName, String projectName, String version, String link) {
            content = texture("breakout.png", 2690 / 4.0, 1466 / 4.0);
            content.setTranslateY(bg.getHeight());

            bg.setArcWidth(2.5);
            bg.setArcHeight(2.5);

            var btn = getUIFactoryService().newButton("Download");
            btn.setPrefWidth(80);
            btn.fontProperty().unbind();
            btn.setFont(Font.font(18));
            btn.setOnAction(e -> {
                showMessage("1. Downloading: \ngithub.com/AlmasB/FXGL/releases/download/11.11/app-beta-win.zip\n" +
                        "2. Unzipping ...\n" +
                        "3. Running ...");
            });
            btn.setCursor(Cursor.HAND);

            btn.setTranslateX(bg.getWidth() - btn.getPrefWidth() - 10);
            btn.setTranslateY(10);

            var projectNameText = new Text(projectName);
            projectNameText.setFont(Font.font(24));

            var projectVersionText = getUIFactoryService().newText(version, Color.BLACK, 18);
            var authorNameText = getUIFactoryService().newText(authorName, Color.BLACK, 14.0);
            var projectLinkText = getUIFactoryService().newText(link, Color.BLACK, FontType.MONO, 14.0);

            var box = new HBox(15, projectNameText, projectVersionText);
            //box.setTranslateX(10);
            //box.setTranslateY(5);

            var vbox = new VBox(10, box, authorNameText, projectLinkText);
            vbox.setPadding(new Insets(10));

            getChildren().addAll(bg, vbox, btn);

            setEffect(new DropShadow(5, Color.BLACK));

            setOnMouseClicked(e -> {
                if (isCollapsed) {
                    getChildren().remove(content);
                } else {
                    getChildren().add(content);
                }

                isCollapsed = !isCollapsed;
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
