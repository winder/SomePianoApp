package com.willwinder.rtp.util;

import com.willwinder.rtp.model.Key;

public class NoteEvent {
    public final Key key;
    public final Boolean active;

    public NoteEvent(Key key, Boolean active) {
        this.key = key;
        this.active = active;
    }
}
