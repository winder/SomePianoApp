package com.willwinder.rtp.graphics.renderables;

import com.google.common.eventbus.Subscribe;
import com.willwinder.rtp.graphics.Renderable;
import com.willwinder.rtp.model.params.TimelineParams;
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
        int offset = 0;
        if (!this.params.out.get()) {
            offset = +3000;
        }
        final int finalOffset = offset;
        synchronized(sparks) {
            long now = System.currentTimeMillis();
            // Add a new spark on press.
            if (event.active) {
                TimelineSpark spark = new TimelineSpark(now + finalOffset, -1, false, event.key.key);
                sparks.computeIfAbsent(event.key.key, k -> new ArrayList<>())
                        .add(spark);
            }
            // Set end time on release.
            else {
                sparks.computeIfPresent(event.key.key, (k, v) -> {
                    v.get(v.size() - 1).endTimeMs = now + finalOffset;
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
        long topMs = 0;
        long duration = this.params.timelineDuration.get().toMillis();
        if(this.params.out.get()) {
            topMs = p.nowMs - duration;
        } else {
            topMs = p.nowMs + duration;
        }

        // Process the sparks
        synchronized (sparks) {
            for (var sparkList : this.sparks.values()) {
                var iter = sparkList.listIterator();
                while (iter.hasNext()) {
                    var spark = iter.next();

                    long ageCutoff = this.params.out.get() ? topMs : p.nowMs;

                    // If it's too old, remove it.
                    if (spark.endTimeMs > 0 && spark.endTimeMs < ageCutoff) {
                        iter.remove();
                    }
                    // Otherwise draw it.
                    else {
                        drawSpark(gc, spark, topMs, p.canvasWidth, p.canvasHeight, p.nowMs);
                    }
                }
            }
        }
    }

    /**
     * Compute the spark size, and then draw it.
     * @param gc GraphicsContext where the spark will be drawn.
     * @param spark metadata associated with the spark.
     * @param topMs timestamp in milliseconds of the top of the timeline. "now" is at the bottom.
     * @param w width of the timeline canvas.
     * @param h height of the timeline canvas.
     * @param now precomputed now timestamp to ensure all sparks are aligned.
     */
    private void drawSpark(GraphicsContext gc, TimelineSpark spark, long topMs, double w, double h, long now) {
        // Get X dimensions (key width)
        var points = this.params.keyPointCache.getPoints(spark.key);

        // TODO: Handle unknown keys
        if (points == null) return;

        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        for (double p : points.xPoints) {
            if (p < xMin) xMin = p;
            if (p > xMax) xMax = p;
        }

        // Get Y dimensions (key press timestamps)
        double timelineHeight = h - params.keyPointCache.getWhiteKeyHeight();
        long duration = this.params.timelineDuration.get().toMillis();

        double yMax = 0.0;
        double yMin = 0.0;

        // Notes are outgoing
        if (this.params.out.get()) {
            if (spark.endTimeMs > 0) {
                long range = spark.endTimeMs - topMs;
                yMax = range / (double) duration * timelineHeight;
            } else {
                yMax = timelineHeight;
            }

            if (spark.startTimeMs > topMs) {
                long range = spark.startTimeMs - topMs;
                yMin = range / (double) duration * timelineHeight;
            } else {
                yMin = 0;
            }
        }
        // Notes are incoming
        else {
            if (spark.endTimeMs > 0) {
                long range = topMs - spark.endTimeMs;
                yMax = range / (double) duration * timelineHeight;
            } else {
                yMax = 0;
            }

            if (spark.startTimeMs > now) {
                long range = topMs - spark.startTimeMs;
                yMin = range / (double) duration * timelineHeight;
            } else {
                yMin = timelineHeight;
            }
        }

        gc.setFill(Color.RED);

        gc.fillRect(
                xMin,
                yMin,
                Math.min(xMax - xMin, w),
                Math.min(yMax - yMin, h - params.keyPointCache.getWhiteKeyHeight()));
    }
}
