package com.almasb.fxhub.ui;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public final class Header extends StackPane {

    public Header(String name) {
        var text = getUIFactoryService().newText(name + " on " + getSettings().getPlatform(), Color.BLACK, 22.0);
        text.setTranslateX(15);

        var bg = new Rectangle(getAppWidth(), 40, Color.WHITE);

        setEffect(new DropShadow(5, Color.BLACK));

        setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(bg, text);
    }
}