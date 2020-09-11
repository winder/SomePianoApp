package com.willwinder.rtp.graphics;

import com.google.common.eventbus.Subscribe;
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

    private final KeyPointCache cache;
    private final TimelineSparksParams params;
    private final HashMap<Integer, List<TimelineSpark>> sparks = new HashMap<>();
    private long nextCleanupTime = 0;

    public static class TimelineSparksParams {
        public final double xLeftMargin;
        public final double xRightMargin;
        public final double yTopMargin;
        public final Duration timelineDuration;

        /**
         * TimelineSpark parameters. The bottom margine is dynamic based on how large
         * the cache says white keys are.
         * @param xLeftMargin margin along the left edge of the timeline.
         * @param xRightMargin margin along the right edge of the timeline.
         * @param yTopMargin margin along the top of the timeline.
         * @param timelineDuration the duration represented by the timeline.
         */
        public TimelineSparksParams(double xLeftMargin, double xRightMargin, double yTopMargin, Duration timelineDuration) {
            this.xLeftMargin = xLeftMargin;
            this.xRightMargin = xRightMargin;
            this.yTopMargin = yTopMargin;
            this.timelineDuration = timelineDuration;
        }
    }

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
                v.get(v.size()-1).endTimeMs = now;
                return v;
            });
        }
    }

    /**
     * Create the timeline spark renderable.
     * @param cache
     * @param params
     */
    public TimelineSparks(KeyPointCache cache, TimelineSparksParams params) {
        this.cache = cache;
        this.params = params;
        this.nextCleanupTime = System.currentTimeMillis() + CLEANUP_INTERVAL.toMillis();
    }

    /**
     * Draw the sparks.
     * @param gc the GraphicsContext where the drawing should be made.
     * @param reset indicates that the display has been reset and a full redraw should occur.
     * @param scale the desired scale of the drawing.
     * @throws RenderableException
     */
    @Override
    public void draw(GraphicsContext gc, boolean reset, double scale) throws RenderableException {
        double w = gc.getCanvas().getWidth();
        double h = gc.getCanvas().getHeight();

        long now = System.currentTimeMillis();
        long topMs = now - this.params.timelineDuration.toMillis();
        long bottomMs = now;
        // Clear the spark area, everything always needs to be redrawn.
        gc.setFill(Color.BLACK);
        gc.fillRect(
                params.xLeftMargin,
                params.yTopMargin,
                w - params.xRightMargin,
                h - cache.getWhiteKeyHeight());

        // Process the sparks
        for (var sparkList : this.sparks.values()) {
            var iter = sparkList.listIterator();
            while (iter.hasNext()) {
                var spark = iter.next();

                // If it's too hold, remove it.
                if (spark.endTimeMs > 0 && spark.endTimeMs < topMs) {
                    iter.remove();
                }
                // Otherwise draw it.
                else {
                    drawSpark(gc, spark, topMs, w, h);
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
        var points = this.cache.getPoints(spark.key);

        // TODO: Handle unknown points
        if (points == null) return;
        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        for (double p : points.xPoints) {
            if (p < xMin) xMin = p;
            if (p > xMax) xMax = p;
        }

        // Get Y dimensions (key press timestamps)
        double timelineHeight = h - cache.getWhiteKeyHeight() - params.yTopMargin;
        double yMin = 0;
        double yMax = h - cache.getWhiteKeyHeight();
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
                Math.min(yMax - yMin, h - cache.getWhiteKeyHeight()));
    }
}
