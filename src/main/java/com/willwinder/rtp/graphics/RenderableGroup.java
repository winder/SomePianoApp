package com.willwinder.rtp.graphics;

import javafx.scene.canvas.GraphicsContext;

import java.util.Arrays;
import java.util.List;

/**
 * Used to group together related renderables. This allows for things like limiting how frequently they are redrawn,
 * but ensures they will be redrawn on the same frame.
 */
public class RenderableGroup implements Renderable {
    private final List<Renderable> renderableList;

    public RenderableGroup(Renderable ... r) {
        renderableList = Arrays.asList(r);
    }

    @Override
    public void draw(GraphicsContext gc, DrawParams params) throws RenderableException {
        for(Renderable r : renderableList) {
            r.draw(gc, params);
        }
    }
}
