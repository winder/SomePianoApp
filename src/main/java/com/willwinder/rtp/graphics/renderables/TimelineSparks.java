package com.willwinder.rtp.graphics.renderables;

import com.google.common.eventbus.Subscribe;
import com.willwinder.rtp.graphics.Renderable;
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
        int offset = 0;
        if (!this.params.out) {
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
        if(this.params.out) {
            topMs = p.nowMs - this.params.timelineDuration.toMillis();
        } else {
            topMs = p.nowMs + this.params.timelineDuration.toMillis();
        }

        // Process the sparks
        synchronized (sparks) {
            for (var sparkList : this.sparks.values()) {
                var iter = sparkList.listIterator();
                while (iter.hasNext()) {
                    var spark = iter.next();

                    long ageCutoff = this.params.out ? topMs : p.nowMs;

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
     * @param gc
     * @param spark
     * @param topMs
     * @param w
     * @param h
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
        double timelineHeight = h - params.keyPointCache.getWhiteKeyHeight() - params.yTopMargin;
        long duration = this.params.timelineDuration.toMillis();

        double yMax = 0.0;
        double yMin = 0.0;

        // Notes are outgoing
        if (this.params.out) {
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
        // Notes are incomming
        else {
            if (spark.endTimeMs > 0) {
                long range = topMs - spark.endTimeMs;
                yMax = range / (double) duration * timelineHeight;
            } else {
                yMax = 0;
            }

            //System.out.println("Diff topms - now: " + (topMs - now));
            //System.out.println("Diff topms - start: " + (topMs - spark.startTimeMs));
            //System.out.println("Diff start - now: " + (spark.startTimeMs - now));
            if (spark.startTimeMs > now) {
                long range = topMs - spark.startTimeMs;
                yMin = range / (double) duration * timelineHeight;
            } else {
                yMin = timelineHeight;
            }
        }

        gc.setFill(Color.RED);

        gc.fillRect(
                params.xLeftMargin + xMin,
                params.yTopMargin + yMin,
                Math.min(xMax - xMin, w - params.xRightMargin),
                Math.min(yMax - yMin, h - params.keyPointCache.getWhiteKeyHeight()));
    }

    private static double getYEndOffset(long timeMs, long topMs, double timelineHeight, long duration) {
        long diff = timeMs - topMs;
        if (timeMs > 0) {
            long range = timeMs - topMs;
            return range / (double) duration * timelineHeight;
        } else {
            return timelineHeight;
        }
    }

    private static double getYStartOffset(long timeMs, long topMs, double timelineHeight, long duration) {
        long diff = timeMs - topMs;
        if (timeMs > topMs) {
            long range = timeMs - topMs;
            return range / (double) duration * timelineHeight;
        } else {
            return 0;
        }

    }
}
