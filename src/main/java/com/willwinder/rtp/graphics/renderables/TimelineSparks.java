package com.willwinder.rtp.graphics;

import com.google.common.eventbus.Subscribe;
import com.willwinder.rtp.model.TimelineParams;
import com.willwinder.rtp.util.NoteEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Render sparks in the timeline to represent pressed keys.
 */
public class TimelineSparks implements Renderable {
    private static Duration CLEANUP_INTERVAL = Duration.ofSeconds(5);

    private final TimelineParams params;
    private final HashMap<Integer, List<TimelineSpark>> sparks = new HashMap<>();
    private long nextCleanupTime = 0;

    private static class TimelineSpark {
        private final long startTimeMs;
        // Not final because the end time is updated later.
        private long endTimeMs;
        private final boolean sustain;
        private final int key;

        private TimelineSpark(long startTime, long endTime, boolean sustain, int key) {
            this.startTimeMs = startTime;
            this.endTimeMs = endTime;
            this.sustain = sustain;
            this.key = key;
        }
    }

    /**
     * Used to notify the timeline of note begin and end events.
     */
    @Subscribe
    public void noteEvent(NoteEvent event) {
        synchronized(sparks) {
            long now = System.currentTimeMillis();
            // Add a new spark on press.
            if (event.active) {
                TimelineSpark spark = new TimelineSpark(now, -1, false, event.key.key);
                sparks.computeIfAbsent(event.key.key, k -> new ArrayList<>())
                        .add(spark);
            }
            // Set end time on release.
            else {
                sparks.computeIfPresent(event.key.key, (k, v) -> {
                    v.get(v.size() - 1).endTimeMs = now;
                    return v;
                });
            }
        }
    }

    /**
     * Create the timeline spark renderable.
     * @param params
     */
    public TimelineSparks(TimelineParams params) {
        this.params = params;
        this.nextCleanupTime = System.currentTimeMillis() + CLEANUP_INTERVAL.toMillis();
    }

    /**
     * Draw the sparks.
     * @param gc the GraphicsContext where the drawing should be made.
     * @param p the params object full of useful bits and bobs for the Renderables.
     * @throws RenderableException
     */
    @Override
    public void draw(GraphicsContext gc, DrawParams p) throws RenderableException {
        long topMs = p.nowMs - this.params.timelineDuration.toMillis();

        // Process the sparks
        synchronized (sparks) {
            for (var sparkList : this.sparks.values()) {
                var iter = sparkList.listIterator();
                while (iter.hasNext()) {
                    var spark = iter.next();

                    // If it's too old, remove it.
                    if (spark.endTimeMs > 0 && spark.endTimeMs < topMs) {
                        iter.remove();
                    }
                    // Otherwise draw it.
                    else {
                        drawSpark(gc, spark, topMs, p.canvasWidth, p.canvasHeight);
                    }
                }
            }
        }
    }

    /**
     * Compute the spark size, and then draw it.
     * @param gc
     * @param spark
     * @param topMs
     * @param w
     * @param h
     */
    private void drawSpark(GraphicsContext gc, TimelineSpark spark, long topMs, double w, double h) {
        // Ended after timeline duration, don't render.
        if (spark.endTimeMs > 0 && spark.endTimeMs < topMs) {
            return;
        }

        // Get X dimensions (key width)
        var points = this.params.keyPointCache.getPoints(spark.key);

        // TODO: Handle unknown points
        if (points == null) return;
        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        for (double p : points.xPoints) {
            if (p < xMin) xMin = p;
            if (p > xMax) xMax = p;
        }

        // Get Y dimensions (key press timestamps)
        double timelineHeight = h - params.keyPointCache.getWhiteKeyHeight() - params.yTopMargin;
        double yMin = 0;
        double yMax = h - params.keyPointCache.getWhiteKeyHeight();
        if (spark.endTimeMs > 0) {
            long range = spark.endTimeMs - topMs;
            yMax = range / (double) this.params.timelineDuration.toMillis() * timelineHeight;
        }
        if (spark.startTimeMs > topMs) {
            long range = spark.startTimeMs - topMs;
            yMin = range / (double) this.params.timelineDuration.toMillis() * timelineHeight;
        }


        gc.setFill(Color.RED);

        gc.fillRect(
                params.xLeftMargin + xMin,
                params.yTopMargin + yMin,
                Math.min(xMax - xMin, w - params.xRightMargin),
                Math.min(yMax - yMin, h - params.keyPointCache.getWhiteKeyHeight()));
    }
}
