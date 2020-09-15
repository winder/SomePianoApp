package com.willwinder.rtp.util;

import com.willwinder.rtp.model.Key;

public class NoteEvent {
    public final int track;
    public final Key key;
    public final long timestampMs;

    public NoteEvent(Key key, int track, long timestampMs) {
        this.key = key;
        this.track = track;
        this.timestampMs = timestampMs;
    }
}
