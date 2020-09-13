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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import java.time.Duration;

import static com.willwinder.rtp.Constants.*;

/**
 * Currently this is pretty much a manual DI layer where the parameters and objects are glued together.
 */
public class Main extends Application {

    private class AllParams {
        private final KeyPointCache keyPointCache;
        private final KeyboardView.KeyboardViewParams keyboardViewParams;
        private final TimelineParams timelineParams;
        private final BPMLines.BPMParams bpmParams;
        private final EventBus eventBus;
        private final KeyboardState keyboardState;
        private final KeyboardReceiver keyboardReceiver;

        private AllParams(KeyPointCache keyPointCache, KeyboardView.KeyboardViewParams keyboardViewParams, TimelineParams timelineParams, BPMLines.BPMParams bpmParams, EventBus eventBus, KeyboardState keyboardState, KeyboardReceiver keyboardReceiver) {
            this.keyPointCache = keyPointCache;
            this.keyboardViewParams = keyboardViewParams;
            this.timelineParams = timelineParams;
            this.bpmParams = bpmParams;
            this.eventBus = eventBus;
            this.keyboardState = keyboardState;
            this.keyboardReceiver = keyboardReceiver;
        }
    }

    private AnimationTimer initializeAnimation(GraphicsContext gc, AllParams params) {

        AnimationController ac = new AnimationController(gc);
        KeyboardView keyboardView = new KeyboardView(params.keyboardState, params.keyboardViewParams, params.keyPointCache);


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

        // Toolbar pane
        ToolBar leftToolBar = new ToolBar();
        ToolBar centerToolBar = new ToolBar();
        ToolBar rightToolBar = new ToolBar();
        leftToolBar.opacityProperty().setValue(0.15);
        centerToolBar.opacityProperty().setValue(0.15);
        rightToolBar.opacityProperty().setValue(0.15);

        // Align the 3 toolbar areas
        HBox toolBarAlignmentPane = new HBox(leftToolBar, centerToolBar, rightToolBar);
        toolBarAlignmentPane.setAlignment(Pos.CENTER);
        HBox.setHgrow(leftToolBar, Priority.ALWAYS);
        HBox.setHgrow(centerToolBar, Priority.ALWAYS);

        BorderPane toolBarPane = new BorderPane();
        toolBarPane.setTop(toolBarAlignmentPane);

        // Stack UI layers.
        StackPane mainPane = new StackPane();
        mainPane.getChildren().addAll(canvasPane, toolBarPane);

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

        KeyboardView.KeyboardViewParams keyboardViewParams = new KeyboardView.KeyboardViewParams(
                5.0,
                200.0,
                true,
                5,
                4,
                1);
        KeyPointCache keyPointCache = new KeyPointCache(
                DEFAULT_HEIGHT,
                0.0,
                0.0,
                12,
                65,
                22.3,
                100,
                1,
                1,
                36,
                49);

        TimelineParams timelineParams = new TimelineParams(
                true,
                Duration.ofSeconds(3),
                keyPointCache);
        BPMLines.BPMParams bpmParams = new BPMLines.BPMParams(
                100,
                timelineParams);

        AllParams params = new AllParams(
                keyPointCache,
                keyboardViewParams,
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
