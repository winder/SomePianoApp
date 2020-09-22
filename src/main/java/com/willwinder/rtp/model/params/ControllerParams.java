package com.willwinder.rtp.model.params;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

import java.util.Arrays;

public class ControllerParams {
    public enum Mode {
        LISTEN,
        RHYTHM,
        FOLLOW,
        PLAY,
        REALTIME
    }
    public enum Hands {
        LEFT,
        RIGHT,
        BOTH
    }

    public static SimpleListProperty<Mode> modeOptionsListProperty = new SimpleListProperty<>(FXCollections.observableList(Arrays.asList(Mode.values())));
    public static SimpleListProperty<Hands> handsOptionsListProperty = new SimpleListProperty<>(FXCollections.observableList(Arrays.asList(Hands.values())));

    public ObjectProperty<Mode> mode;
    public ObjectProperty<Hands> hands;

    public ControllerParams(Mode mode, Hands hands) {
        this.mode = new SimpleObjectProperty<>(mode);
        this.hands = new SimpleObjectProperty<>(hands);
    }
}
