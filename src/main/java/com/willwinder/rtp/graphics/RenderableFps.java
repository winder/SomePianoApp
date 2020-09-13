package com.willwinder.rtp.graphics;

import javafx.scene.canvas.GraphicsContext;

public class RenderableFps implements Renderable {
    private final Renderable r;
    private final int fps;
    private final double delayMs;

    private long nextDraw = 0;

    public RenderableFps(Renderable r, int fps) {
        this.r = r;
        this.fps = fps;
        this.delayMs = 1 / (double) fps;
    }


    @Override
    public void draw(GraphicsContext gc, DrawParams params) throws RenderableException {
        if (nextDraw < params.nowMs) {
            r.draw(gc, params);
            nextDraw = params.nowMs + (long) (delayMs * 1000);
        }
    }
}
