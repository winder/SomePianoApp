package com.willwinder.rtp.view;

import com.willwinder.rtp.model.MainModel;
import com.willwinder.rtp.util.BorderToolBar;
import com.willwinder.rtp.util.CanvasPane;
import com.willwinder.rtp.util.GraphicButton;
import com.willwinder.rtp.util.NoteEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;

import static com.willwinder.rtp.util.Util.midiMessageToKey;
import static com.willwinder.rtp.util.Util.midiMessageToString;
import static javax.sound.midi.MidiSystem.getSequence;

public class MainView extends StackPane {
    public GraphicsContext graphicsContext;
    public ObjectProperty<EventHandler<ActionEvent>> settingsEvent;
    public ObjectProperty<EventHandler<ActionEvent>> openMidiFileEvent;
    public ObjectProperty<EventHandler<ActionEvent>> playMidiFileEvent;

    public MainView(MainModel model) {
        CanvasPane canvas = new CanvasPane(0, 0);
        graphicsContext = canvas.canvas.getGraphicsContext2D();
        BorderPane canvasPane = new BorderPane(canvas);

        BorderToolBar toolBar = new BorderToolBar(45, 0.25);

        // Settings button
        FontIcon settingsIcon = FontIcon.of(FontAwesome.SLIDERS, 30, Color.WHITE);
        Button settings = new GraphicButton(settingsIcon, Color.DARKGRAY);
        settingsEvent = settings.onActionProperty();

        FontIcon audioFile = FontIcon.of(FontAwesome.FILE_AUDIO_O, 30, Color.WHITE);
        Button openMidiFile = new GraphicButton(audioFile, Color.DARKGRAY);
        openMidiFileEvent = openMidiFile.onActionProperty();

        FontIcon playFile = FontIcon.of(FontAwesome.PLAY, 30, Color.WHITE);
        Button playMidiFile = new GraphicButton(playFile, Color.DARKGRAY);
        playMidiFile.disableProperty().bind(model.midiFileSequence.isNull());
        playMidiFileEvent = playMidiFile.onActionProperty();


        toolBar.addRight(settings);
        toolBar.addCenter(playMidiFile);
        toolBar.addLeft(openMidiFile);

        // Add UI to StackPane
        getChildren().addAll(canvasPane, toolBar);
    }
}
