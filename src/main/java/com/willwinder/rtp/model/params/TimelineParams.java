package com.willwinder.rtp.model.params;

import com.willwinder.rtp.graphics.KeyPointCache;
import javafx.beans.property.*;

import java.time.Duration;

public class TimelineParams {
    public final IntegerProperty timelineDurationMs;
    public final KeyPointCache keyPointCache;
    public final BooleanProperty out;

    /**
     * Timeline parameters. The bottom margine is dynamic based on how large
     * the cache says white keys are.
     * @param out true if the timeline should scroll outwards from the keyboard, otherwise it will scroll inwards.
     * @param timelineDuration the duration represented by the timeline.
     * @param keyPointCache
     */
    public TimelineParams(boolean out, Duration timelineDuration, KeyPointCache keyPointCache) {
        this.out = new SimpleBooleanProperty(out);
        this.timelineDurationMs = new SimpleIntegerProperty((int)timelineDuration.toMillis());
        this.keyPointCache = keyPointCache;
    }

    public double getHeight(double canvasHeight) {
        return canvasHeight - keyPointCache.getWhiteKeyHeight();
    }
}
