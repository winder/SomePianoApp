package com.willwinder.rtp.model.params;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class KeyPointCacheParams {
    public final DoubleProperty leftMargin;
    public final DoubleProperty rightMargin;
    public final DoubleProperty blackKeyWidthRatio;
    public final DoubleProperty blackKeyHeightRatio;
    public final DoubleProperty whiteKeyHeightRatio;
    public final DoubleProperty padding;
    public final IntegerProperty firstKey;
    public final IntegerProperty numKeys;


    /**
     * @param leftMargin          margin to the left of the keys.
     * @param rightMargin         margin to the right of the keys
     * @param blackKeyWidthRatio  ratio between white key width and black key width.
     * @param blackKeyHeightRatio ratio between white key height and black key height.
     * @param whiteKeyHeightRatio ratio from white key width to white key height.
     * @param padding             padding around keys, removed from the size, not added.
     * @param firstKey            the first key on keyboard, MIDI key code (A0 = 21).
     * @param numKeys             number of keys on keyboard.
     */
    public KeyPointCacheParams(double leftMargin,
                               double rightMargin,
                               double blackKeyWidthRatio,
                               double blackKeyHeightRatio,
                               double whiteKeyHeightRatio,
                               double padding,
                               int firstKey,
                               int numKeys) {
        this.leftMargin = new SimpleDoubleProperty(leftMargin);
        this.rightMargin = new SimpleDoubleProperty(rightMargin);
        this.blackKeyWidthRatio = new SimpleDoubleProperty(blackKeyWidthRatio);
        this.blackKeyHeightRatio = new SimpleDoubleProperty(blackKeyHeightRatio);
        this.whiteKeyHeightRatio = new SimpleDoubleProperty(whiteKeyHeightRatio);
        this.padding = new SimpleDoubleProperty(padding);
        this.firstKey = new SimpleIntegerProperty(firstKey);
        this.numKeys = new SimpleIntegerProperty(numKeys);
    }
}
