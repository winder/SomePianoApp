package com.willwinder.rtp.graphics;

import javafx.scene.canvas.GraphicsContext;

/**
 * An encapsulated component which can be drawn to a JavaFX canvas. This is a very simple Actor model.
 */
public interface Renderable {
    class RenderableException extends Exception {

    }

    class DrawParams {
        public final double canvasWidth;
        public final double canvasHeight;
        public final boolean reset;
        public final long nowMs;

        /**
         * @param canvasWidth current width of the canvas.
         * @param canvasHeight current height of the canvas.
         * @param reset indicates that the display has been reset and a full redraw should occur.
         * @param nowMs the current time in milliseconds, passed in since many renderables need it.
         */
        public DrawParams(double canvasWidth, double canvasHeight, boolean reset, long nowMs) {
            this.canvasWidth = canvasWidth;
            this.canvasHeight = canvasHeight;
            this.reset = reset;
            this.nowMs = nowMs;
        }
    }

    /**
     * Called when this component should be drawn.
     * @param gc the GraphicsContext where the drawing should be made.
     * @param params the params object full of useful bits and bobs for the Renderables.
     */
    void draw(GraphicsContext gc, DrawParams params) throws RenderableException;
}
