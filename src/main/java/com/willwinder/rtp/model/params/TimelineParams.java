package com.willwinder.rtp.model.params;

import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.model.TimelineNotes;
import javafx.beans.property.*;

import java.time.Duration;
import java.util.ArrayList;

public class TimelineParams {
    public final ArrayList<TimelineNotes.TimelineNote> midiNotes;
    public final TimelineNotes playerNotes;
    public final IntegerProperty timelineDurationMs;
    public final KeyPointCache keyPointCache;
    public final BooleanProperty out;
    public final LongProperty nowMs;
    public final DoubleProperty quarterNoteDurationMs;
    public final DoubleProperty measureDurationMs;

    /**
     * Timeline parameters. The bottom margine is dynamic based on how large
     * the cache says white keys are.
     * @param playerNotes
     * @param out true if the timeline should scroll outwards from the keyboard, otherwise it will scroll inwards.
     * @param timelineDuration the duration represented by the timeline.
     * @param keyPointCache
     */
    public TimelineParams(TimelineNotes playerNotes, boolean out, Duration timelineDuration, KeyPointCache keyPointCache) {
        this.midiNotes = new ArrayList<>();
        this.playerNotes = playerNotes;
        this.out = new SimpleBooleanProperty(out);
        this.timelineDurationMs = new SimpleIntegerProperty((int)timelineDuration.toMillis());
        this.keyPointCache = keyPointCache;
        this.nowMs = new SimpleLongProperty(0);
        this.quarterNoteDurationMs = new SimpleDoubleProperty(0);
        this.measureDurationMs = new SimpleDoubleProperty();
        this.measureDurationMs.bind(this.quarterNoteDurationMs.multiply(4.0));
    }

    public double getHeight(double canvasHeight) {
        return canvasHeight - keyPointCache.getWhiteKeyHeight();
    }
}
