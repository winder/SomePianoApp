package com.willwinder.rtp;

import com.google.common.eventbus.EventBus;

import com.willwinder.rtp.controller.AnimationController;
import com.willwinder.rtp.controller.MainController;
import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.model.MainModel;
import com.willwinder.rtp.model.TimelineNotes;
import com.willwinder.rtp.model.params.*;
import com.willwinder.rtp.util.KeyboardReceiver;
import com.willwinder.rtp.view.MainView;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static com.willwinder.rtp.Constants.DEFAULT_HEIGHT;
import static com.willwinder.rtp.Constants.DEFAULT_WIDTH;

/**
 * Currently this is pretty much a manual DI layer where the parameters and objects are glued together.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws MidiUnavailableException {
        AllParams allParams = initializeAllParams();

        // Setup the MIDI keyboard
        MidiSystem
                .getTransmitter()
                .setReceiver(allParams.keyboardReceiver);

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");

        MainModel model = new MainModel();
        MainView mainPane = new MainView(model, allParams, stage);
        MainController mainController = new MainController(model, allParams, stage);

        mainPane.openMidiFileEvent.set(mainController.openMidiFileActionHandler);
        mainPane.playMidiFileEvent.set(mainController.playMidiFileActionHandler);

        Scene scene = new Scene(mainPane, DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
        stage.setTitle("RealTime Piano");
        stage.setScene(scene);

        // start the animation timer.
        AnimationTimer at = new AnimationController(mainPane.graphicsContext, allParams);
        at.start();

        try {
            model.midiFileSequence.setValue(MidiSystem.getSequence(new File("/home/will/Downloads/Prelude_I_in_C_major_BWV_846_-_Well_Tempered_Clavier_First_Book.mid")));
        } catch (Exception e) {
            System.out.println("This should have been removed.");
        }

        // Display the GUI
        stage.show();
    }

    /**
     * There are all the knobs which can be tuned initialized in one place.
     *
     * // TODO: This should involve saving/loading parameters somehow.
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

        var notes = new TimelineNotes();
        eventBus.register(notes);

        TimelineParams timelineParams = new TimelineParams(
                notes,
                false,
                Duration.ofSeconds(3),
                keyPointCache);
        BPMParams bpmParams = new BPMParams(
                100,
                timelineParams);

        GrandStaffParams staffParams = new GrandStaffParams(
                80.0,
                0.3
        );

        AllParams params = new AllParams(
                keyPointCacheParams,
                keyPointCache,
                timelineParams,
                bpmParams,
                staffParams,
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
