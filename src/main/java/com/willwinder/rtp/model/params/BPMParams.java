package com.willwinder.rtp.model.params;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BPMParams {
    public IntegerProperty bpm;
    public TimelineParams timelineParams;

    public BPMParams(int bpm, TimelineParams timelineParams) {
        this.bpm = new SimpleIntegerProperty(bpm);
        this.timelineParams = timelineParams;
    }
}