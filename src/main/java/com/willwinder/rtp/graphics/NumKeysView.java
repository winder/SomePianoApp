package com.willwinder.rtp.graphics;

import com.willwinder.rtp.KeyboardState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Draw the number of keys being pressed.
 */
public class NumKeysView implements Renderable {

    private KeyboardState state;

    public NumKeysView(KeyboardState state) {
        this.state = state;
    }

    @Override
    public void draw(GraphicsContext gc, double scale) throws RenderableException {
        gc.setFill(Color.WHITE);
        gc.fillText(String.valueOf(state.getActiveKeys().size()), 10.0, 10.0);
    }
}
