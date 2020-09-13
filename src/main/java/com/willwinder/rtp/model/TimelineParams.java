package com.willwinder.rtp.model;

import com.willwinder.rtp.graphics.KeyPointCache;

import java.time.Duration;

public class TimelineParams {
    public final double xLeftMargin;
    public final double xRightMargin;
    public final double yTopMargin;
    public final Duration timelineDuration;
    public final KeyPointCache keyPointCache;
    public final boolean out;

    /**
     * Timeline parameters. The bottom margine is dynamic based on how large
     * the cache says white keys are.
     * @param xLeftMargin margin along the left edge of the timeline.
     * @param xRightMargin margin along the right edge of the timeline.
     * @param yTopMargin margin along the top of the timeline.
     * @param out true if the timeline should scroll outwards from the keyboard, otherwise it will scroll inwards.
     * @param timelineDuration the duration represented by the timeline.
     * @param keyPointCache
     */
    public TimelineParams(double xLeftMargin, double xRightMargin, double yTopMargin, boolean out, Duration timelineDuration, KeyPointCache keyPointCache) {
        this.xLeftMargin = xLeftMargin;
        this.xRightMargin = xRightMargin;
        this.yTopMargin = yTopMargin;
        this.out = out;
        this.timelineDuration = timelineDuration;
        this.keyPointCache = keyPointCache;
    }

    public double getHeight(double canvasHeight) {
        return canvasHeight - keyPointCache.getWhiteKeyHeight() - yTopMargin;
    }
}
