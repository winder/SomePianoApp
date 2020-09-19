package com.willwinder.rtp.model;

import com.google.common.eventbus.Subscribe;
import com.willwinder.rtp.util.NoteEvent;

import java.util.*;

public class TimelineNotes implements Iterable<List<TimelineNotes.TimelineNote>> {
    // Using a hashmap to make updates more efficient.
    // Consumers just know that it is a collection of lists of timeline notes.
    private final HashMap<Integer, List<TimelineNotes.TimelineNote>> notes;

    @Override
    public Iterator<List<TimelineNote>> iterator() {
        return notes.values().iterator();
    }

    public static class TimelineNote {
        public final long startTimeMs;
        // Not final because the end time is updated later.
        public long endTimeMs;
        public final boolean sustain;
        public final int track;
        public final Key key;

        private TimelineNote(long startTime, long endTime, boolean sustain, int track, Key key) {
            this.startTimeMs = startTime;
            this.endTimeMs = endTime;
            this.sustain = sustain;
            this.track = track;
            this.key = key;
        }
    }

    public TimelineNotes() {
        notes = new HashMap<>();
    }

    /**
     * Used to notify the timeline of note begin and end events.
     */
    @Subscribe
    synchronized public void noteEvent(NoteEvent event) {
        long now = System.currentTimeMillis();
        // Add a new spark on press.
        if (event.key.isActive()) {
            TimelineNote note = new TimelineNote(event.timestampMs, -1, false, event.track, event.key);
            notes.computeIfAbsent(event.key.key, k -> new ArrayList<>())
                    .add(note);
        }
        // Set end time on release.
        else {
            // Note: Adding this to the LAST note location.
            //       When queuing a MIDI song, the notes should all be
            //       added quickly, and one key at a time. Hopefully it
            //       doesn't try to play the same key multiple times. If
            //       if it does, hopefully it's done on different tracks.
            notes.computeIfPresent(event.key.key, (k, v) -> {
                v.get(v.size() - 1).endTimeMs = event.timestampMs;
                return v;
            });
        }
    }

    // From time to time it may be useful to clear things out.
    public void cleanup() {
        notes.clear();
    }
}
