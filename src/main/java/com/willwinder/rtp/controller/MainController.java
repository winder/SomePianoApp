package com.willwinder.rtp.controller;

import com.willwinder.rtp.model.MainModel;
import com.willwinder.rtp.model.params.AllParams;
import com.willwinder.rtp.util.NoteEvent;
import com.willwinder.rtp.util.Util;
import com.willwinder.rtp.view.SettingsView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class MainController {
    private MainModel model;
    private Stage parent;
    private AllParams params;

    private SettingsView settings = null;

    public MainController(AllParams params, Stage parent, MainModel model) {
        this.parent = parent;
        this.model = model;
        this.params = params;
        settings = new SettingsView(params, parent);
    }

    public EventHandler<ActionEvent> settingsActionHandler = event -> {
        if (settings == null) {
            settings = new SettingsView(params, parent);
        }
        settings.showAndWait();
    };


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

    public EventHandler<ActionEvent> playMidiFileActionHandler = event -> {
        long offset = System.currentTimeMillis() + params.timelineParams.timelineDurationMs.get();

        var seq = this.model.midiFileSequence.get();

        double ppqn = 0.0;
        if (seq.getDivisionType() == 0.0) {
            ppqn = seq.getResolution();
        } else {
            ppqn = seq.getDivisionType();
        }

        double kMsPerQuarterNote = 60.0 / (double) this.params.bpmParams.bpm.get();
        double kMsPerTick = kMsPerQuarterNote / ppqn;

        int trackNum = 0;
        for (Track track : seq.getTracks()) {
            trackNum++;
            for (int i = 0; i < track.size(); i++) {
                MidiEvent midiEvent = track.get(i);
                var tick = midiEvent.getTick();
                System.out.println(Util.midiMessageToString(midiEvent.getMessage()));
                var key = Util.midiMessageToKey(midiEvent.getMessage());
                if (key.isPresent()) {
                    var delta = tick * kMsPerTick * 1000.0;
                    this.params.eventBus.post(new NoteEvent(key.get(), trackNum, (long) (delta + offset)));
                }
            }
        }
    };
}
