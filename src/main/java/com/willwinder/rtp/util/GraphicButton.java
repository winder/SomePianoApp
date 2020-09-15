package com.willwinder.rtp.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * A button with a FontIcon graphic which changes color when pressed.
 */
public class GraphicButton extends Button {
    private final FontIcon icon;
    private final Paint iconColor;
    private final Paint activeIconColor;
    private final Paint disabledIconColor;

    public GraphicButton(FontIcon icon, Color activeColor) {
        this(icon, activeColor, activeColor);
    }

    public GraphicButton(FontIcon icon, Color activeColor, Color disabledIconColor) {
        this("", icon, activeColor, activeColor);
    }

    public GraphicButton(String label, FontIcon icon, Color activeColor, Color disabledColor) {
        super(label);

        this.icon = icon;
        this.iconColor = icon.getIconColor();
        this.activeIconColor = activeColor;
        this.disabledIconColor = disabledColor;

        //Button settings = new Button();
        setStyle("-fx-background-color: transparent");
        setGraphic(icon);
        setPadding(new Insets(0, 0, 0, 0));

        pressedProperty().addListener(this::pressed);
    }

    public void pressed(ObservableValue observable, Boolean wasPressed, Boolean pressed) {
        if (pressed) {
            icon.setIconColor(activeIconColor);
        } else {
            icon.setIconColor(iconColor);
        }
        setGraphic(icon);
    }
}
