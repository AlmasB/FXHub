package com.almasb.fxstore;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.ColoredTexture;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FXStoreApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXStore");
        settings.setVersion("0.1-SNAPSHOT");
        settings.setHeightFromRatio(16/9.0);
    }

    @Override
    protected void initUI() {
        var title = new Title("FXStore");

        addUINode(title);

        var items = FXCollections.observableArrayList("Author1 - AppName1", "Author2 - AppName2");

        var listView = getUIFactoryService().newListView(items);

        addUINode(listView, 0, 50);

        var image = new ColoredTexture(600, 400, Color.LIGHTSALMON);
        var btn = getUIFactoryService().newButton("Download");
        btn.setOnAction(e -> {
            showMessage("1. Downloading: \ngithub.com/AlmasB/FXGL/releases/download/11.11/app-beta-win.zip\n" +
                    "2. Unzipping ...\n" +
                    "3. Running ...");
        });

        var stack = new StackPane(image, btn);
        stack.setAlignment(Pos.TOP_RIGHT);

        addUINode(stack, 200, 50);
    }

    private static class Title extends StackPane {
        Title(String name) {
            var text = getUIFactoryService().newText(name, Color.BLACK, 22.0);
            text.setTranslateX(15);

            var bg = new Rectangle(getAppWidth(), 50, Color.WHITE);

            setEffect(new DropShadow(10, Color.BLACK));

            setAlignment(Pos.CENTER_LEFT);

            getChildren().addAll(bg, text);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
