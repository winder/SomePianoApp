package com.willwinder.rtp.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.sound.midi.Sequence;

public class MainModel {
    public ObjectProperty<Sequence> midiFileSequence = new SimpleObjectProperty<>(null);
    public BooleanProperty showStaff = new SimpleBooleanProperty(false);
    public BooleanProperty showTimeline = new SimpleBooleanProperty(true);
    public BooleanProperty showKeyboard = new SimpleBooleanProperty(true);
}
