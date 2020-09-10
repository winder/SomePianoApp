package com.willwinder.rtp.util;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

/**
 * A Pane with an embedded Canvas to automatically resize the canvas to its scene.
 */
public class CanvasPane extends Pane {
    final public Canvas canvas;

    public CanvasPane(double width, double height) {
        setWidth(width);
        setHeight(height);
        canvas = new Canvas(width, height);
        getChildren().add(canvas);

        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
    }
}
