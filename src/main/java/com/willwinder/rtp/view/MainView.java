package com.willwinder.rtp.view;

import com.willwinder.rtp.model.MainModel;
import com.willwinder.rtp.model.params.AllParams;
import com.willwinder.rtp.util.BorderToolBar;
import com.willwinder.rtp.util.CanvasPane;
import com.willwinder.rtp.util.GraphicButton;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainView extends StackPane {
    public GraphicsContext graphicsContext;
    public ObjectProperty<EventHandler<ActionEvent>> openMidiFileEvent;
    public ObjectProperty<EventHandler<ActionEvent>> playMidiFileEvent;
    public ObjectProperty<EventHandler<ActionEvent>> pauseMidiFileEvent;

    private final SettingsView settingsView;

    /**
     * Setup the main view and expose some properties for the controller to hook into.
     */
    public MainView(MainModel model, AllParams allParams, Stage parent) {
        this.settingsView = new SettingsView(allParams, parent);

        CanvasPane canvas = new CanvasPane(0, 0);
        graphicsContext = canvas.canvas.getGraphicsContext2D();
        BorderPane canvasPane = new BorderPane(canvas);

        BorderToolBar toolBar = new BorderToolBar(45, 0.25);

        // Settings button
        FontIcon settingsIcon = FontIcon.of(FontAwesome.SLIDERS, 30, Color.WHITE);
        Button settings = new GraphicButton(settingsIcon, Color.DARKGRAY);
        settings.setOnAction((ae) -> settingsView.showAndWait() );

        FontIcon audioFile = FontIcon.of(FontAwesome.FILE_AUDIO_O, 30, Color.WHITE);
        Button openMidiFile = new GraphicButton(audioFile, Color.DARKGRAY);
        openMidiFileEvent = openMidiFile.onActionProperty();

        FontIcon playFile = FontIcon.of(FontAwesome.PLAY, 30, Color.WHITE);
        Button playMidiFile = new GraphicButton(playFile, Color.DARKGRAY);
        playMidiFile.disableProperty().bind(model.midiFileSequence.isNull());
        playMidiFileEvent = playMidiFile.onActionProperty();

        FontIcon pauseFile = FontIcon.of(FontAwesome.PAUSE, 30, Color.WHITE);
        Button pauseMidiFile = new GraphicButton(pauseFile, Color.DARKGRAY);
        pauseMidiFile.disableProperty().bind(model.midiFileSequence.isNull());
        pauseMidiFileEvent = pauseMidiFile.onActionProperty();

        toolBar.addRight(settings);
        toolBar.addCenter(pauseMidiFile);
        toolBar.addCenter(playMidiFile);
        toolBar.addLeft(openMidiFile);

        // Add UI to StackPane
        getChildren().addAll(canvasPane, toolBar);
    }
}
