package com.willwinder.rtp.model.params;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class GrandStaffParams {
    public final DoubleProperty margin;
    public final BooleanProperty top;
    public final DoubleProperty heightPct;
    public final DoubleProperty leftNoteMargin;

    public final DoubleProperty offsetPercentTest;

    public GrandStaffParams(double margin, double heightPct, boolean top) {
        this.margin = new SimpleDoubleProperty(margin);
        this.top = new SimpleBooleanProperty(top);
        this.heightPct = new SimpleDoubleProperty(heightPct);
        this.leftNoteMargin = new SimpleDoubleProperty(0.0);

        this.offsetPercentTest = new SimpleDoubleProperty(0.0);
    }
}
