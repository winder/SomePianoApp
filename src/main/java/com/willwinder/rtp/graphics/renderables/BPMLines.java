package com.willwinder.rtp.graphics;

import com.willwinder.rtp.model.TimelineParams;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.time.Duration;

public class BPM implements Renderable {
    public static class BPMParams {
        public int bpm;
        public TimelineParams timelineParams;

        public BPMParams(int bpm, TimelineParams timelineParams) {
            this.bpm = bpm;
            this.timelineParams = timelineParams;
        }
    }

    private final BPMParams params;


    public BPM(BPMParams params) {
        this.params = params;
    }

    private double yBeatOffset = 0.0;

    /**
     * @param gc    the GraphicsContext where the drawing should be made.
     * @param p the params object full of useful bits and bobs for the Renderables.
     * @throws RenderableException
     */
    @Override
    public void draw(GraphicsContext gc, DrawParams p) throws RenderableException {
        long msPerBeat = Duration.ofMinutes(1).toMillis() / params.bpm;
        long duration = this.params.timelineParams.timelineDuration.toMillis();
        long topMs = p.nowMs - duration;
        double timelineHeight = p.canvasHeight - this.params.timelineParams.keyPointCache.getWhiteKeyHeight() - params.timelineParams.yTopMargin;

        if (p.reset || this.yBeatOffset == 0.0) {
            // recompute yBeatOffset
            this.yBeatOffset = msPerBeat / (double) duration * timelineHeight;
        }

        double x1 = params.timelineParams.xLeftMargin;
        double x2 = p.canvasWidth - params.timelineParams.xRightMargin;

        // y offset of the beat line at the top of the canvas
        double firstBeatMs = (topMs - topMs % msPerBeat) + msPerBeat;
        double y = (firstBeatMs - topMs) / duration * timelineHeight;
        double endOffset = timelineHeight - params.timelineParams.yTopMargin;
        for (double yOffset = y; yOffset < endOffset; yOffset += this.yBeatOffset) {
            gc.setStroke(Color.DARKGRAY);
            gc.setLineWidth(1);
            //gc.setLineDashes(10);
            //gc.setLineDashOffset(15);
            gc.strokeLine(x1, yOffset, x2, yOffset);
        }
    }
}
