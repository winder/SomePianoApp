package com.willwinder.rtp;

import com.willwinder.rtp.controller.AnimationController;
import com.willwinder.rtp.graphics.KeyboardView;
import com.willwinder.rtp.graphics.NumKeysView;
import com.willwinder.rtp.util.CanvasPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import static com.willwinder.rtp.Constants.DEFAULT_HEIGHT;
import static com.willwinder.rtp.Constants.DEFAULT_WIDTH;

/**
 * Currently this is pretty much a manual DI layer where the parameters and objects are glued together.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws MidiUnavailableException {
        // Setup the MIDI keyboard
        var receiver = new KeyboardReceiver();
        MidiSystem
                .getTransmitter()
                .setReceiver(receiver);

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");

        CanvasPane canvas = new CanvasPane(0, 0);
        GraphicsContext gc = canvas.canvas.getGraphicsContext2D();
        BorderPane border = new BorderPane(canvas);

        AnimationController ac = new AnimationController(gc, receiver);
        KeyboardView.KeyboardViewParams kbvParams = new KeyboardView.KeyboardViewParams(
                5.0,
                200.0,
                true,
                12,
                65,
                22.5,
                100,
                5,
                4,
                1);
        ac.addRenderable(new KeyboardView(receiver, kbvParams));
        ac.addRenderable(new NumKeysView(receiver));
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
