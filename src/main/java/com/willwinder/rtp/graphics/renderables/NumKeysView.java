package com.willwinder.rtp.graphics;

import com.willwinder.rtp.KeyboardState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Draw the number of keys being pressed.
 */
public class NumKeysView implements Renderable {

    private final KeyboardState state;

    public NumKeysView(KeyboardState state) {
        this.state = state;
    }

    @Override
    public void draw(GraphicsContext gc, DrawParams p) {
        gc.setFill(Color.WHITE);
        gc.fillText(String.valueOf(state.getActiveKeys().size()), 10.0, 10.0);
    }
}
