package com.willwinder.rtp.model.params;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class GrandStaffParams {
    public final DoubleProperty topMargin;
    public final DoubleProperty heightPct;
    public final DoubleProperty leftNoteMargin;

    public final DoubleProperty offsetPercentTest;

    public GrandStaffParams(double topMargin, double heightPct) {
        this.topMargin = new SimpleDoubleProperty(topMargin);
        this.heightPct = new SimpleDoubleProperty(heightPct);
        this.leftNoteMargin = new SimpleDoubleProperty(0.0);

        this.offsetPercentTest = new SimpleDoubleProperty(0.0);
    }
}
