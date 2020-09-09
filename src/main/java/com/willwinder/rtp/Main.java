package com.willwinder.rtp;

import com.willwinder.rtp.controller.AnimationController;
import com.willwinder.rtp.util.CanvasPane;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.canvas.*;

import javax.sound.midi.*;

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
        ac.start();

        Scene scene = new Scene(border, 640, 480, Color.BLACK);
        stage.setTitle("Lines");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
