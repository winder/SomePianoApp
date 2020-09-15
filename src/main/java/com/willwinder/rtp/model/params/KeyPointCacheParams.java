package com.willwinder.rtp.model.params;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;

import java.util.Objects;

public class KeyPointCacheParams {
    public final DoubleProperty leftMargin;
    public final DoubleProperty rightMargin;
    public final DoubleProperty blackKeyWidthRatio;
    public final DoubleProperty blackKeyHeightRatio;
    public final DoubleProperty whiteKeyHeightRatio;
    public final DoubleProperty padding;
    public final IntegerProperty firstKey;
    public final IntegerProperty numKeys;

    private int hashCache = 0;

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

        // Reset the hash cache when something changes.
        ChangeListener listener = (ChangeListener<Number>) (observable, oldValue, newValue) -> hashCache = 0;
        this.leftMargin.addListener(listener);
        this.rightMargin.addListener(listener);
        this.blackKeyWidthRatio.addListener(listener);
        this.blackKeyHeightRatio.addListener(listener);
        this.whiteKeyHeightRatio.addListener(listener);
        this.padding.addListener(listener);
        this.firstKey.addListener(listener);
        this.numKeys.addListener(listener);
    }

    @Override
    public int hashCode() {
        if (hashCache == 0) {
            hashCache = Objects.hash(
                    this.leftMargin.get(),
                    this.rightMargin.get(),
                    this.blackKeyWidthRatio.get(),
                    this.blackKeyHeightRatio.get(),
                    this.whiteKeyHeightRatio.get(),
                    this.padding.get(),
                    this.firstKey.get(),
                    this.numKeys.get());
        }

        return hashCache;
    }
}
