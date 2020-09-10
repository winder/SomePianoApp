package com.willwinder.rtp.graphics;

import javafx.scene.canvas.GraphicsContext;

/**
 * An encapsulated component which can be drawn to a JavaFX canvas. This is a very simple Actor model.
 */
public interface Renderable {
    class RenderableException extends Exception {

    }

    /**
     * Called when this component should be drawn.
     * @param gc the {@GraphicsContext} where the drawing should be made.
     * @param scale the desired scale of the drawing.
     * @throws RenderableException
     */
    void draw(GraphicsContext gc, double scale) throws RenderableException;
}
