package com.willwinder.rtp.controller;

import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.model.MainModel;
import com.willwinder.rtp.model.TimelineNotes;
import com.willwinder.rtp.model.params.AllParams;
import com.willwinder.rtp.util.NoteEvent;
import com.willwinder.rtp.util.Util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller.
 */
public class MainController {
    private final GraphicsContext gc;
    private KeyPointCache keyPointCache;
    private AllParams allParams;
    private MainModel model;
    private Stage parent;

    ////////////////////////
    // Playback metadata. //
    ////////////////////////
    private ArrayList<TimelineNotes.TimelineNote> midiNotes;
    private int noteIdx = 0;
    private boolean playing = false;
    private boolean paused = false;
    private long lastUpdateMs = 0;
    private long songOffsetMs = 0;

    public MainController(GraphicsContext gc, AllParams params, MainModel model, Stage parent) {
        this.gc = gc;
        this.keyPointCache = params.keyPointCache;
        this.allParams = params;
        this.model = model;
        this.parent = parent;

        model.midiFileSequence.addListener(s -> loadMidiFile());
    }

    // Set the playback time based on parameters
    public EventHandler<ActionEvent> updateTimelineTimeEvent = event -> updateTime();
    private void updateTime() {
        long now = System.currentTimeMillis();

        // Realtime mode - simple. Exit early when complete
        if (this.allParams.timelineParams.out.get()) {
            this.allParams.timelineParams.nowMs.setValue(now);
            return;
        }

        // Playback mode
        long start = songOffsetMs;
        long end = songOffsetMs + this.allParams.timelineParams.timelineDurationMs.get();

        Set<Integer> requiredNotes = new HashSet<>();
        Set<Integer> optionalNotes = new HashSet<>();
        //String r = "";
        //String o = "";
        for (var note : this.allParams.timelineParams.midiNotes) {
            var len = note.endTimeMs - note.startTimeMs;
            if (note.startTimeMs <= start && note.endTimeMs > start) {
                // Make the note optional after the first half has been played
                if (note.startTimeMs + (len/2.0) > start) {
                    //r += " " + note.key.note + note.key.octave;
                    requiredNotes.add(note.key.key);
                } else {
                    //o += " " + note.key.note + note.key.octave;
                    optionalNotes.add(note.key.key);
                }
            }
        }
        //System.out.println("required: " + r + ", optional: " + o);

        var activeNotes = allParams.timelineParams.playerNotes.getActiveNotes();
        boolean missingOrExtra = false;
        if (requiredNotes.size() > 0) {
            int foundNum = 0;
            for (var active : activeNotes) {
                // Check if the note was required, and that it was activated within 500ms
                boolean foundRequired = requiredNotes.contains(active.key.key);
                boolean foundOptional = optionalNotes.contains(active.key.key);

                if (foundRequired) {
                    foundNum++;
                } else if (foundOptional) {
                    // cool
                } else {
                    missingOrExtra = true;
                    System.out.println("Extra: " + active.key.note + active.key.octave);
                    break;
                }
            }
            missingOrExtra = missingOrExtra || foundNum < requiredNotes.size();
        }

        if (!playing || paused || missingOrExtra) {
            this.lastUpdateMs = 0;
        } else {
            if (this.lastUpdateMs != 0) {
                long delta = now - this.lastUpdateMs;
                songOffsetMs += delta;
                this.allParams.timelineParams.nowMs.setValue(songOffsetMs);
            }
            this.lastUpdateMs = now;
        }

        // Update timeline midi notes
        // Remove ones that are no longer visible.
        var iter = this.allParams.timelineParams.midiNotes.iterator();
        while (iter.hasNext()) {
            var note = iter.next();
            if (note.endTimeMs < start) iter.remove();
        }

        // Add new notes coming onto the timeline.
        while (noteIdx < this.midiNotes.size() && this.midiNotes.get(noteIdx).startTimeMs < end) {
            this.allParams.timelineParams.midiNotes.add(
                    this.midiNotes.get(noteIdx));
            this.noteIdx++;
        }
    }

    public EventHandler<ActionEvent> openMidiFileActionHandler = event -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File f = fileChooser.showOpenDialog(parent);
        if (f != null) {
            Sequence sequence = null;
            try {
                sequence = MidiSystem.getSequence(f);
            } catch (InvalidMidiDataException invalidMidiDataException) {
                invalidMidiDataException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            if (sequence != null) {
                model.midiFileSequence.setValue(sequence);
            }
        }
    };

    private void loadMidiFile() {
        if (this.model.midiFileSequence.isNull().get()) {
            return;
        }

        long duration = this.allParams.timelineParams.timelineDurationMs.get();
        this.songOffsetMs = -duration;
        this.noteIdx = 0;
        this.allParams.timelineParams.nowMs.setValue(-duration);

        var seq = this.model.midiFileSequence.get();

        double ppqn;
        if (seq.getDivisionType() == 0.0) {
            ppqn = seq.getResolution();
        } else {
            ppqn = seq.getDivisionType();
        }

        double msPerQuarterNote = 60000.0 / (double) this.allParams.bpmParams.bpm.get();
        double msPerTick = msPerQuarterNote / ppqn;
        this.allParams.timelineParams.quarterNoteDurationMs.setValue(msPerQuarterNote);

        int trackNum = 0;
        TimelineNotes midiNotes = new TimelineNotes();

        for (Track track : seq.getTracks()) {
            trackNum++;
            for (int i = 0; i < track.size(); i++) {
                MidiEvent midiEvent = track.get(i);
                var tick = midiEvent.getTick();

                String msg = Util.midiMessageToString(midiEvent.getMessage());
                if (msg.length() > 0) {
                    System.out.println(msg);
                }

                var key = Util.midiMessageToKey(midiEvent.getMessage());
                if (key.isPresent()) {
                    var delta = tick * msPerTick;
                    midiNotes.noteEvent(new NoteEvent(key.get(), trackNum, (long) delta));
                }
            }
        }

        this.midiNotes = midiNotes.getSortedNotesArray();
    }

    public EventHandler<ActionEvent> pauseMidiFileActionHandler = event -> this.paused = true;

    public EventHandler<ActionEvent> playMidiFileActionHandler = event -> {
        // Check if this is a "resume" button, in which case resume and don't reset.
        if (this.playing && this.paused) {
            this.paused = false;
            return;
        }

        // Reset play information.
        this.lastUpdateMs = 0;
        this.songOffsetMs = -this.allParams.timelineParams.timelineDurationMs.get();
        this.playing = true;
    };
}
