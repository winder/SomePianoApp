package com.willwinder.rtp.controller;

import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.graphics.Renderable;
import com.willwinder.rtp.graphics.RenderableFps;
import com.willwinder.rtp.graphics.RenderableGroup;
import com.willwinder.rtp.graphics.renderables.*;
import com.willwinder.rtp.model.MainModel;
import com.willwinder.rtp.model.params.AllParams;
import com.willwinder.rtp.util.NoteEvent;
import com.willwinder.rtp.util.Util;
import com.willwinder.rtp.view.SettingsView;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main loop, and is sort of an controller in the MVC sense of the word. The JavaFX runtime is
 * responsible for calling handle when the canvas should be repainted.
 *
 * Drawing is done using a simple Actor model. Any number of Renderable objects can be added for drawing.
 */
public class MainController extends AnimationTimer {
    private final GraphicsContext gc;
    private KeyPointCache keyPointCache;
    private AllParams allParams;
    private MainModel model;
    private Stage parent;
    private final List<Renderable> renderableList = new ArrayList<>();

    // For detecting a reset is required.
    private double w, h = 0.0;
    private int currentKeyPointHash = 0;

    // Playback metadata.
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
        KeyboardView keyboardView = new KeyboardView(params.keyboardState, params.keyPointCache);

        TimelineBackground timelineBackground = new TimelineBackground(params.timelineParams);
        TimelineSparks timelineSparks = new TimelineSparks(params.timelineParams, params.keyboardState);

        GrandStaff grandStaff = new GrandStaff(params.timelineParams, params.grandStaffParams);

        BPMLines bpm = new BPMLines(params.bpmParams);

        RenderableGroup timeline = new RenderableGroup(
                timelineBackground,
                bpm,
                timelineSparks,
                grandStaff
        );
        RenderableFps timelineFps = new RenderableFps(timeline, 40);

        //////////////////////////
        // Register renderables //
        //////////////////////////
        addRenderable(keyboardView);
        addRenderable(timelineBackground);
        addRenderable(bpm);
        addRenderable(timelineSparks);
        addRenderable(grandStaff);

        ////////////////////////
        // Limit Timeline FPS //
        ////////////////////////
        //ac.addRenderable(keyboardView);
        //ac.addRenderable(timelineFps);

        ///////////////
        // Debugging //
        ///////////////
        //ac.addRenderable(new NumKeysView(receiver));

        // Register listeners
        params.eventBus.register(keyboardView);
        //params.eventBus.register(timelineSparks);

        start();
    }

    // Set the playback time based on parameters
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

    public void addRenderable(Renderable r) {
        if (!renderableList.contains(r)) {
            renderableList.add(r);
        }
    }

    public void removeRenderable(Renderable r) {
        renderableList.remove(r);
    }

    @Override
    public void handle(long l) {
        updateTime();

        double height = gc.getCanvas().getHeight();
        double width = gc.getCanvas().getWidth();

        boolean reset = false;
        int kpHash = this.keyPointCache.params.hashCode();
        if (w != width || h != height || currentKeyPointHash != kpHash) {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, width, height);
            this.w = width;
            this.h = height;
            this.currentKeyPointHash = kpHash;
            reset = true;
        }

        Renderable.DrawParams drawParams = new Renderable.DrawParams(w, h, reset, this.allParams.timelineParams.nowMs.get());

        for (Renderable r : renderableList) {
            try {
                r.draw(gc, drawParams);
            } catch (Renderable.RenderableException e) {
                e.printStackTrace();
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
