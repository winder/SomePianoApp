package com.willwinder.rtp.graphics.renderables;

import com.willwinder.rtp.graphics.Renderable;
import com.willwinder.rtp.model.params.TimelineParams;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TimelineBackground implements Renderable {
    private final TimelineParams params;

    public TimelineBackground(TimelineParams params) {
        this.params = params;
    }

    @Override
    public void draw(GraphicsContext gc, DrawParams p) throws RenderableException {
        // Clear the spark area, everything always needs to be redrawn.
        gc.setFill(Color.BLACK);
        gc.fillRect(
                0,
                0,
                p.canvasWidth,
                p.canvasHeight - params.keyPointCache.getWhiteKeyHeight());
        /*
        // Centerline
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(p.canvasWidth / 2 - 2, 0,
                4, p.canvasHeight - params.keyPointCache.getWhiteKeyHeight());
         */
    }
}
