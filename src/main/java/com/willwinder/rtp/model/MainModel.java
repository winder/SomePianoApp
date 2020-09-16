package com.willwinder.rtp.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.sound.midi.Sequence;

public class MainModel {
    public ObjectProperty<Sequence> midiFileSequence = new SimpleObjectProperty<>(null);
}
