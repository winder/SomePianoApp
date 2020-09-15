package com.willwinder.rtp;

import com.willwinder.rtp.controller.AnimationController;
import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.graphics.RenderableFps;
import com.willwinder.rtp.graphics.RenderableGroup;
import com.willwinder.rtp.graphics.renderables.BPMLines;
import com.willwinder.rtp.graphics.renderables.KeyboardView;
import com.willwinder.rtp.graphics.renderables.TimelineBackground;
import com.willwinder.rtp.graphics.renderables.TimelineSparks;
import com.willwinder.rtp.model.params.AllParams;
import com.willwinder.rtp.model.params.BPMParams;
import com.willwinder.rtp.model.params.KeyPointCacheParams;
import com.willwinder.rtp.model.params.TimelineParams;
import com.willwinder.rtp.util.*;
import com.willwinder.rtp.view.SettingsView;

import com.google.common.eventbus.EventBus;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static com.willwinder.rtp.Constants.DEFAULT_HEIGHT;
import static com.willwinder.rtp.Constants.DEFAULT_WIDTH;
import static com.willwinder.rtp.util.Util.midiMessageToKey;
import static com.willwinder.rtp.util.Util.midiMessageToString;
import static javax.sound.midi.MidiSystem.getSequence;

/**
 * Currently this is pretty much a manual DI layer where the parameters and objects are glued together.
 */
public class Main extends Application {
    private ObjectProperty<Sequence> loadedSequence = new SimpleObjectProperty<>(null);

    private AnimationTimer initializeAnimation(GraphicsContext gc, AllParams params) {

        AnimationController ac = new AnimationController(gc, params.keyPointCache);
        KeyboardView keyboardView = new KeyboardView(params.keyboardState, params.keyPointCache);


        TimelineBackground timelineBackground = new TimelineBackground(params.timelineParams);
        TimelineSparks timelineSparks = new TimelineSparks(params.timelineParams, params.keyboardState);

        BPMLines bpm = new BPMLines(params.bpmParams);

        RenderableGroup timeline = new RenderableGroup(
                timelineBackground,
                bpm,
                timelineSparks
        );
        RenderableFps timelineFps = new RenderableFps(timeline, 40);

        //////////////////////////
        // Register renderables //
        //////////////////////////
        ac.addRenderable(keyboardView);
        ac.addRenderable(timelineBackground);
        ac.addRenderable(bpm);
        ac.addRenderable(timelineSparks);

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
        params.eventBus.register(timelineSparks);

        return ac;
    }

    @Override
    public void start(Stage stage) throws MidiUnavailableException {
        AllParams allParams = initializeAllParams();

        // Setup the MIDI keyboard
        MidiSystem
                .getTransmitter()
                .setReceiver(allParams.keyboardReceiver);

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");

        // Setup JavaFX

        // Canvas pane
        CanvasPane canvas = new CanvasPane(0, 0);
        GraphicsContext gc = canvas.canvas.getGraphicsContext2D();
        BorderPane canvasPane = new BorderPane(canvas);

        BorderToolBar toolBar = new BorderToolBar(45, 0.25);

        // Settings button
        FontIcon settingsIcon = FontIcon.of(FontAwesome.SLIDERS, 30, Color.WHITE);
        Button settings = new GraphicButton(settingsIcon, Color.DARKGRAY);
        settings.setOnAction(e -> showSettings(allParams, stage));

        /*
        // Test a setting
        FontIcon arrowIcon = FontIcon.of(FontAwesome.ARROWS_V, 30, Color.WHITE);
        Button toggleOut = new GraphicButton(arrowIcon, Color.DARKGRAY);
        toggleOut.setOnAction(e -> {
            boolean out = allParams.timelineParams.out.get();
            allParams.timelineParams.out.setValue(!out);
        });
        toolBar.addLeft(toggleOut);
         */

        FontIcon audioFile = FontIcon.of(FontAwesome.FILE_AUDIO_O, 30, Color.WHITE);
        Button openMidiFile = new GraphicButton(audioFile, Color.DARKGRAY);
        openMidiFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File f = fileChooser.showOpenDialog(stage);
            if (f != null) {
                Sequence sequence = null;
                try {
                    sequence = getSequence(f);
                } catch (InvalidMidiDataException invalidMidiDataException) {
                    invalidMidiDataException.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                if (sequence != null) {
                    this.loadedSequence.setValue(sequence);
                }
            }
        });

        FontIcon playFile = FontIcon.of(FontAwesome.PLAY, 30, Color.WHITE);
        Button playMidiFile = new GraphicButton(playFile, Color.DARKGRAY);
        playMidiFile.disableProperty().bind(this.loadedSequence.isNull());
        playMidiFile.setOnAction((e) -> {
            long offset = System.currentTimeMillis() + allParams.timelineParams.timelineDurationMs.get();

            var seq = this.loadedSequence.get();


            double ppqn = 0.0;
            if (seq.getDivisionType() == 0.0) {
                ppqn = this.loadedSequence.get().getResolution();
            } else {
                ppqn = seq.getDivisionType();
            }

            //double tickDurationMs = (1.0/ppqn);

            double kMsPerQuarterNote = 60.0 / (double) allParams.bpmParams.bpm.get();
            double kMsPerTick = kMsPerQuarterNote / ppqn;

            /*
            float kMillisecondsPerQuarterNote = tempo / 1000.0f;
            float kMillisecondsPerTick = kMillisecondsPerQuarterNote / ppqn;
            float deltaTimeInMilliseconds = tick * kMillisecondsPerTick;

             */

            int trackNum = 0;
            for (Track track : this.loadedSequence.get().getTracks()) {
                trackNum++;
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent midiEvent = track.get(i);
                    var tick = midiEvent.getTick();
                    System.out.println(midiMessageToString(midiEvent.getMessage()));
                    var key = midiMessageToKey(midiEvent.getMessage());
                    if (key.isPresent()) {
                        var delta = tick * kMsPerTick * 1000.0;
                        allParams.eventBus.post(new NoteEvent(key.get(), trackNum, (long) (delta + offset)));
                    }
                }
            }
        });

        toolBar.addRight(settings);
        toolBar.addCenter(playMidiFile);
        toolBar.addLeft(openMidiFile);

        // Stack UI layers.
        StackPane mainPane = new StackPane();
        mainPane.getChildren().addAll(canvasPane, toolBar);

        Scene scene = new Scene(mainPane, DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
        stage.setTitle("RealTime Piano");
        stage.setScene(scene);

        // start the animation timer.
        var ac = initializeAnimation(gc, allParams);
        ac.start();

        // Display the GUI
        stage.show();
    }

    private void showSettings(AllParams params, Stage parent) {
        var view = new SettingsView(params, parent);
    }

    /**
     * There are all the knobs which can be tuned initialized in one place.
     * @return
     */
    private AllParams initializeAllParams() {
        EventBus eventBus = new EventBus();
        KeyboardReceiver receiver = new KeyboardReceiver(eventBus);

        KeyPointCacheParams keyPointCacheParams = new KeyPointCacheParams(
                50.0,
                50.0,
                0.53,
                0.65,
                4.5,
                1,
                36,
                49);
                // 88 key piano parameters.
                //21,
                //88);

        KeyPointCache keyPointCache = new KeyPointCache(keyPointCacheParams);

        TimelineParams timelineParams = new TimelineParams(
                false,
                Duration.ofSeconds(3),
                keyPointCache);
        BPMParams bpmParams = new BPMParams(
                100,
                timelineParams);

        AllParams params = new AllParams(
                keyPointCacheParams,
                keyPointCache,
                timelineParams,
                bpmParams,
                eventBus,
                receiver,
                receiver);

        return params;
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
