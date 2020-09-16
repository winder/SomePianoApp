package com.willwinder.rtp;

import com.google.common.eventbus.EventBus;
import com.willwinder.rtp.controller.AnimationController;
import com.willwinder.rtp.controller.MainController;
import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.graphics.RenderableFps;
import com.willwinder.rtp.graphics.RenderableGroup;
import com.willwinder.rtp.graphics.renderables.BPMLines;
import com.willwinder.rtp.graphics.renderables.KeyboardView;
import com.willwinder.rtp.graphics.renderables.TimelineBackground;
import com.willwinder.rtp.graphics.renderables.TimelineSparks;
import com.willwinder.rtp.model.MainModel;
import com.willwinder.rtp.model.params.AllParams;
import com.willwinder.rtp.model.params.BPMParams;
import com.willwinder.rtp.model.params.KeyPointCacheParams;
import com.willwinder.rtp.model.params.TimelineParams;
import com.willwinder.rtp.util.KeyboardReceiver;
import com.willwinder.rtp.view.MainView;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import java.time.Duration;

import static com.willwinder.rtp.Constants.DEFAULT_HEIGHT;
import static com.willwinder.rtp.Constants.DEFAULT_WIDTH;

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

        MainModel model = new MainModel();
        MainView mainPane = new MainView(model);
        MainController mainController = new MainController(allParams, stage, model);

        mainPane.openMidiFileEvent.set(mainController.openMidiFileActionHandler);
        mainPane.playMidiFileEvent.set(mainController.playMidiFileActionHandler);
        mainPane.settingsEvent.set(mainController.settingsActionHandler);

        Scene scene = new Scene(mainPane, DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
        stage.setTitle("RealTime Piano");
        stage.setScene(scene);

        // start the animation timer.
        var ac = initializeAnimation(mainPane.graphicsContext, allParams);
        ac.start();

        // Display the GUI
        stage.show();
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
