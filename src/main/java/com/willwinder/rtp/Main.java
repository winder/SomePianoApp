package com.willwinder.rtp;

import com.willwinder.rtp.controller.AnimationController;
import com.willwinder.rtp.graphics.*;
import com.willwinder.rtp.graphics.renderables.*;
import com.willwinder.rtp.model.params.AllParams;
import com.willwinder.rtp.model.params.BPMParams;
import com.willwinder.rtp.model.params.KeyPointCacheParams;
import com.willwinder.rtp.model.params.TimelineParams;
import com.willwinder.rtp.util.BorderToolBar;
import com.willwinder.rtp.util.CanvasPane;

import com.google.common.eventbus.EventBus;
import com.willwinder.rtp.util.GraphicButton;
import com.willwinder.rtp.util.KeyboardReceiver;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;

import org.kordamp.ikonli.javafx.FontIcon;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import java.time.Duration;

import static com.willwinder.rtp.Constants.*;

/**
 * Currently this is pretty much a manual DI layer where the parameters and objects are glued together.
 */
public class Main extends Application {

    private AnimationTimer initializeAnimation(GraphicsContext gc, AllParams params) {

        AnimationController ac = new AnimationController(gc);
        KeyboardView keyboardView = new KeyboardView(params.keyboardState, params.keyPointCache);


        TimelineBackground timelineBackground = new TimelineBackground(params.timelineParams);
        TimelineSparks timelineSparks = new TimelineSparks(params.timelineParams);

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
        settings.setOnAction(e -> System.out.println("View settings"));

        toolBar.addRight(settings);

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
                true,
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
