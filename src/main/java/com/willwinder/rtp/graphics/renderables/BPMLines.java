package com.willwinder.rtp.graphics.renderables;

import com.google.common.graph.Graph;
import com.willwinder.rtp.graphics.Renderable;
import com.willwinder.rtp.model.TimelineParams;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.time.Duration;

public class BPMLines implements Renderable {
    public static class BPMParams {
        public int bpm;
        public TimelineParams timelineParams;

        public BPMParams(int bpm, TimelineParams timelineParams) {
            this.bpm = bpm;
            this.timelineParams = timelineParams;
        }
    }

    private final BPMParams params;


    public BPMLines(BPMParams params) {
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
        double timelineHeight = this.params.timelineParams.getHeight(p.canvasHeight);
        if (p.reset || this.yBeatOffset == 0.0) {
            // recompute yBeatOffset
            this.yBeatOffset = msPerBeat / (double) duration * timelineHeight;
        }

        double y = 0;
        if (this.params.timelineParams.out) {
            y = outgoingOffset(gc, p, msPerBeat, duration, timelineHeight);
        } else {
            y = incommingOffset(gc, p, msPerBeat, duration, timelineHeight);
        }

        double x1 = params.timelineParams.xLeftMargin;
        double x2 = p.canvasWidth - params.timelineParams.xRightMargin;
        double endOffset = timelineHeight - params.timelineParams.yTopMargin;
        for (double yOffset = y; yOffset < endOffset; yOffset += this.yBeatOffset) {
            gc.setStroke(Color.DARKGRAY);
            gc.setLineWidth(1);
            //gc.setLineDashes(10);
            //gc.setLineDashOffset(15);
            gc.strokeLine(x1, yOffset, x2, yOffset);
        }
    }

    private double incommingOffset(GraphicsContext gc, DrawParams p, double msPerBeat, long duration, double timelineHeight) {
        long topMs = p.nowMs + duration;
        double firstBeatMs = (topMs - topMs % msPerBeat) + msPerBeat;
        return (p.nowMs - firstBeatMs) / duration * timelineHeight;
    }

    private double outgoingOffset(GraphicsContext gc, DrawParams p, double msPerBeat, long duration, double timelineHeight) {
        long topMs = p.nowMs - duration;
        // y offset of the beat line at the top of the canvas
        double firstBeatMs = (topMs - topMs % msPerBeat) + msPerBeat;
        return (firstBeatMs - topMs) / duration * timelineHeight;
    }
}
