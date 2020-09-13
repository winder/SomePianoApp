package com.willwinder.rtp;

import com.willwinder.rtp.controller.AnimationController;
import com.willwinder.rtp.graphics.*;
import com.willwinder.rtp.graphics.renderables.*;
import com.willwinder.rtp.model.KeyboardState;
import com.willwinder.rtp.model.TimelineParams;
import com.willwinder.rtp.util.CanvasPane;

import com.google.common.eventbus.EventBus;
import com.willwinder.rtp.util.KeyboardReceiver;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import java.time.Duration;

import static com.willwinder.rtp.Constants.DEFAULT_HEIGHT;
import static com.willwinder.rtp.Constants.DEFAULT_WIDTH;

/**
 * Currently this is pretty much a manual DI layer where the parameters and objects are glued together.
 */
public class Main extends Application {

    private AnimationTimer initializeAnimation(GraphicsContext gc, EventBus eventBus, KeyboardState state) {
        boolean out = true;

        AnimationController ac = new AnimationController(gc);
        KeyboardView.KeyboardViewParams kbvParams = new KeyboardView.KeyboardViewParams(
                5.0,
                200.0,
                true,
                5,
                4,
                1);
        KeyPointCache keyPointCache = new KeyPointCache(
                DEFAULT_HEIGHT,
                0.0,
                12,
                65,
                22.3,
                100,
                1,
                1,
                36,
                49);
        KeyboardView keyboardView = new KeyboardView(state, kbvParams, keyPointCache);

        TimelineParams timelineParams = new TimelineParams(
                0,
                0,
                0,
                out,
                Duration.ofSeconds(3),
                keyPointCache);

        TimelineBackground timelineBackground = new TimelineBackground(timelineParams);
        TimelineSparks timelineSparks = new TimelineSparks(timelineParams);

        BPMLines.BPMParams bpmParams = new BPMLines.BPMParams(
                100,
                timelineParams);
        BPMLines bpm = new BPMLines(bpmParams);

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
        eventBus.register(keyboardView);
        eventBus.register(timelineSparks);

        return ac;
    }

    @Override
    public void start(Stage stage) throws MidiUnavailableException {
        EventBus eventBus = new EventBus();

        // Setup the MIDI keyboard
        var receiver = new KeyboardReceiver(eventBus);
        MidiSystem
                .getTransmitter()
                .setReceiver(receiver);

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");

        // Setup JavaFX
        CanvasPane canvas = new CanvasPane(0, 0);
        GraphicsContext gc = canvas.canvas.getGraphicsContext2D();
        BorderPane border = new BorderPane(canvas);
        Scene scene = new Scene(border, DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
        stage.setTitle("RealTime Piano");
        stage.setScene(scene);

        // Configure the animation timer.
        var ac = initializeAnimation(gc, eventBus, receiver);

        // Begin animation and show the window
        ac.start();
        stage.show();
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
