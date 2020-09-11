package com.willwinder.rtp;

import com.willwinder.rtp.controller.AnimationController;
import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.graphics.KeyboardView;
import com.willwinder.rtp.graphics.NumKeysView;
import com.willwinder.rtp.graphics.TimelineSparks;
import com.willwinder.rtp.util.CanvasPane;

import com.google.common.eventbus.EventBus;
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

        CanvasPane canvas = new CanvasPane(0, 0);
        GraphicsContext gc = canvas.canvas.getGraphicsContext2D();
        BorderPane border = new BorderPane(canvas);

        AnimationController ac = new AnimationController(gc);
        KeyboardView.KeyboardViewParams kbvParams = new KeyboardView.KeyboardViewParams(
                5.0,
                200.0,
                true,
                5,
                4,
                1);
        KeyPointCache kpCache = new KeyPointCache(
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
        KeyboardView keyboardView = new KeyboardView(receiver, kbvParams, kpCache);

        TimelineSparks.TimelineSparksParams tlsParams = new TimelineSparks.TimelineSparksParams(
                0,
                0,
                0,
                Duration.ofSeconds(10));
        TimelineSparks timelineSparks = new TimelineSparks(kpCache, tlsParams);

        // Register renderables
        ac.addRenderable(keyboardView);
        ac.addRenderable(timelineSparks);
        ac.addRenderable(new NumKeysView(receiver));

        // Register listeners
        eventBus.register(keyboardView);
        eventBus.register(timelineSparks);

        // Begin animation
        ac.start();

        Scene scene = new Scene(border, DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
        stage.setTitle("RealTime Piano");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
