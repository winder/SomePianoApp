package com.willwinder.rtp.model.params;

import javafx.beans.property.*;

public class GrandStaffParams {
    public final IntegerProperty rows;
    public final BooleanProperty descending;
    public final DoubleProperty margin;
    public final BooleanProperty top;
    public final DoubleProperty heightPct;
    public final DoubleProperty leftNoteMargin;

    public final DoubleProperty offsetPercentTest;

    public GrandStaffParams(double margin, double heightPct, boolean top, int rows, boolean descending) {
        this.rows = new SimpleIntegerProperty(rows);
        this.descending = new SimpleBooleanProperty(descending);
        this.margin = new SimpleDoubleProperty(margin);
        this.top = new SimpleBooleanProperty(top);
        this.heightPct = new SimpleDoubleProperty(heightPct);
        this.leftNoteMargin = new SimpleDoubleProperty(0.0);

        this.offsetPercentTest = new SimpleDoubleProperty(0.0);
    }
}
