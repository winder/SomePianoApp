package com.willwinder.rtp.graphics.renderables;

import com.willwinder.rtp.graphics.Renderable;
import com.willwinder.rtp.model.TimelineParams;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TimelineBackground implements Renderable {
    private final TimelineParams params;

    public TimelineBackground(TimelineParams params) {
        this.params = params;
    }

    @Override
    public void draw(GraphicsContext gc, DrawParams p) throws RenderableException {
        long topMs = p.nowMs - this.params.timelineDuration.toMillis();
        long bottomMs = p.nowMs;
        // Clear the spark area, everything always needs to be redrawn.
        gc.setFill(Color.BLACK);
        gc.fillRect(
                params.xLeftMargin,
                params.yTopMargin,
                p.canvasWidth - params.xRightMargin,
                p.canvasHeight - params.keyPointCache.getWhiteKeyHeight());
    }
}
