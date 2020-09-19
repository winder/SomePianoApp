package com.willwinder.rtp.controller;

import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.model.MainModel;
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

/**
 * This is the main loop and controller in the MVC sense of the word.
 *
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
        // Realtime mode
        if (this.allParams.timelineParams.out.get()) {
            this.allParams.timelineParams.nowMs.setValue(now);
        }
        // Playback mode (i.e. check if we are "paused")
        else {
            if (!playing || paused) {
                this.lastUpdateMs = 0;
            } else {
                if (this.lastUpdateMs != 0) {
                    long delta = now - this.lastUpdateMs;
                    songOffsetMs += delta;
                    this.allParams.timelineParams.nowMs.setValue(songOffsetMs);
                }
                this.lastUpdateMs = now;
            }

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
        if (this.model.midiFileSequence.isNotNull().get()) {
            this.allParams.timelineParams.timelineNotes.cleanup();

            long offset = this.allParams.timelineParams.timelineDurationMs.get();

            var seq = this.model.midiFileSequence.get();

            double ppqn;
            if (seq.getDivisionType() == 0.0) {
                ppqn = seq.getResolution();
            } else {
                ppqn = seq.getDivisionType();
            }

            double kMsPerQuarterNote = 60.0 / (double) this.allParams.bpmParams.bpm.get();
            double kMsPerTick = kMsPerQuarterNote / ppqn;

            int trackNum = 0;
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
                        var delta = tick * kMsPerTick * 1000.0;
                        this.allParams.eventBus.post(new NoteEvent(key.get(), trackNum, (long) (delta + offset)));
                    }
                }
            }
        }
    };

    public EventHandler<ActionEvent> pauseMidiFileActionHandler = event -> {
        this.paused = true;
    };

    public EventHandler<ActionEvent> playMidiFileActionHandler = event -> {
        // Check if this is a "resume" button, in which case resume and don't reset.
        if (this.playing && this.paused) {
            this.paused = false;
            return;
        }

        // Reset play information.
        this.lastUpdateMs = 0;
        this.songOffsetMs = 0;
        this.playing = true;
    };
}
