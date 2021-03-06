package com.willwinder.rtp.graphics;

import com.willwinder.rtp.model.Key;
import com.willwinder.rtp.model.params.KeyPointCacheParams;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.HashMap;

public class KeyPointCache {

    // Params
    public final KeyPointCacheParams params;

    // Cache these to trigger recomputing the cache.
    private double height;
    private double width;
    private int paramHashCache;

    // Computed values
    private final DoubleProperty whiteKeyHeight = new SimpleDoubleProperty(0);
    private final DoubleProperty whiteKeyWidth = new SimpleDoubleProperty(0);
    private final DoubleProperty blackKeyHeight = new SimpleDoubleProperty(0);
    private final DoubleProperty blackKeyWidth = new SimpleDoubleProperty(0);

    // Cache
    private final HashMap<Integer, KeyPointCache.KeyPoints> cache = new HashMap<>();

    public KeyPointCache(KeyPointCacheParams params) {
        this.params = params;
        this.height = 0.0;
        this.width = 0.0;
        this.paramHashCache = 0;

        blackKeyWidth.bind(whiteKeyWidth.multiply(params.blackKeyWidthRatio));
        whiteKeyHeight.bind(whiteKeyWidth.multiply(params.whiteKeyHeightRatio));
        blackKeyHeight.bind(whiteKeyHeight.multiply(params.blackKeyHeightRatio));
    }

    public double getBlackKeyHeight() {
        return blackKeyHeight.get();
    }

    public double getBlackKeyWidth() {
        return blackKeyWidth.get();
    }

    public double getWhiteKeyHeight() {
        return whiteKeyHeight.get();
    }

    public double getWhiteKeyWidth() {
        return whiteKeyWidth.get();
    }

    public DoubleProperty getBlackKeyHeightProperty() {
        return blackKeyHeight;
    }

    public DoubleProperty getBlackKeyWidthProperty() {
        return blackKeyWidth;
    }

    public DoubleProperty getWhiteKeyHeightProperty() {
        return whiteKeyHeight;
    }

    public DoubleProperty getWhiteKeyWidthProperty() {
        return whiteKeyWidth;
    }

    /**
     * Reset the cache given a new canvas size.
     * @param newHeight new canvas height.
     * @param newWidth new canvas width.
     */
    public void reset(double newHeight, double newWidth) {
        // If nothing changed and this isn't the first call to reset, this is a no-op.
        if (this.width == newWidth
                && this.height == newHeight
                && this.cache.size() != 0
                && this.paramHashCache == params.hashCode()) return;

        this.height = newHeight;
        this.width = newWidth;
        this.paramHashCache = params.hashCode();
        cache.clear();

        // Compute whiteKeyWidth
        Key.Note note = Key.Note.noteForKey(params.firstKey.get());
        int numWhiteKeys = 0;
        for (int i = 0; i < params.numKeys.get(); i++) {
            if (note.isWhiteKey()) {
                numWhiteKeys++;
            }
            note = note.nextNote();
        }

        whiteKeyWidth.set((width - params.leftMargin.get() - params.rightMargin.get()) / (double) numWhiteKeys);

        note = Key.Note.noteForKey(params.firstKey.get());
        var xStep = getWhiteKeyWidth() / 2.0;
        var xCenter = params.leftMargin.get() + xStep;
        for (int i = params.firstKey.get(); i < params.firstKey.get() + params.numKeys.get(); i++) {
            this.cache.put(i, pointsForKey(
                    note,
                    xCenter,
                    height - getWhiteKeyHeight(),
                    getWhiteKeyHeight(),
                    getWhiteKeyWidth(),
                    getBlackKeyHeight(),
                    getBlackKeyWidth(),
                    params.padding.get()));
            xCenter += xStep;
            // Add another step if there is no sharp.
            if (!note.nextNoteIntervalIsSemitone(Key.Note.C)) {
                xCenter += xStep;
            }
            note = note.nextNote();
        }
    }

    /**
     * Get points for a given key number.
     * @param keyNum the key.
     * @return points for key.
     */
    public KeyPoints getPoints(int keyNum) {
        return cache.get(keyNum);
    }

    /**
     * Internal response tuple for the polygon drawing computation.
     */
    public static class KeyPoints {
        final public double[] xPoints;
        final public double[] yPoints;
        final public int numPoints;

        public KeyPoints(double[] xPoints, double[] yPoints, int numPoints) {
            this.xPoints = xPoints;
            this.yPoints = yPoints;
            this.numPoints = numPoints;
        }
    }

    /**
     * Helper to compute a polygon representing the shape of a requested key. The current
     * algorithm places sharps exactly between the natural keys rather than offsetting them
     * like a real piano would do to make room for the hammer mechanism.
     *
     * TODO: Add a "realistic" mode to offset the sharps and reshape the naturals.
     *
     * @param note the note which the polygon will represent.
     * @param center centerline around which the key will be drawn.
     * @param y y offset where the key will be drawn.
     * @param whiteKeyHeight height of white keys.
     * @param whiteKeyWidth width of white keys.
     * @param blackKeyHeight height of black keys.
     * @param blackKeyWidth width of black keys.
     * @param padding padding around keys.
     * @return centerline offset where the next key would go.
     */
    private static KeyPoints pointsForKey(Key.Note note, double center, double y, double whiteKeyHeight, double whiteKeyWidth, double blackKeyHeight, double blackKeyWidth, double padding) {
        var whiteDiv2 = whiteKeyWidth / 2.0;
        var blackDiv2 = blackKeyWidth / 2.0;

        switch(note) {
            case C:
            case F:
                return new KeyPoints(
                    new double[]{
                            center - whiteDiv2 + padding,
                            center + whiteDiv2 - blackDiv2 - padding,
                            center + whiteDiv2 - blackDiv2 - padding,
                            center + whiteDiv2 - padding,
                            center + whiteDiv2 - padding,
                            center - whiteDiv2 + padding,
                            center - whiteDiv2 + padding,
                    },
                    new double[]{
                            y,
                            y,
                            y + blackKeyHeight + padding,
                            y + blackKeyHeight + padding,
                            y + whiteKeyHeight,
                            y + whiteKeyHeight
                    },
                    6);
            case E:
            case B:
                return new KeyPoints(
                    new double[]{
                            center - whiteDiv2 + blackDiv2 + padding,
                            center + whiteDiv2 - padding,
                            center + whiteDiv2 - padding,
                            center - whiteDiv2 + padding,
                            center - whiteDiv2 + padding,
                            center - whiteDiv2 + blackDiv2 + padding,
                    },
                    new double[]{
                            y,
                            y,
                            y + whiteKeyHeight,
                            y + whiteKeyHeight,
                            y + blackKeyHeight + padding,
                            y + blackKeyHeight + padding,
                    },
                    6);
            case D:
            case G:
            case A:
                return new KeyPoints(
                    new double[]{
                            center - whiteDiv2 + blackDiv2 + padding,
                            center + whiteDiv2 - blackDiv2 - padding,
                            center + whiteDiv2 - blackDiv2 - padding,
                            center + whiteDiv2 - padding,
                            center + whiteDiv2 - padding,
                            center - whiteDiv2 + padding,
                            center - whiteDiv2 + padding,
                            center - whiteDiv2 + blackDiv2 + padding,
                    },
                    new double[]{
                            y,
                            y,
                            y + blackKeyHeight + padding,
                            y + blackKeyHeight + padding,
                            y + whiteKeyHeight,
                            y + whiteKeyHeight,
                            y + blackKeyHeight + padding,
                            y + blackKeyHeight + padding
                    },
                    8);
            case C_SHARP:
            case D_SHARP:
            case F_SHARP:
            case G_SHARP:
            case A_SHARP:
                return new KeyPoints(
                    new double[]{
                            center - blackDiv2 + padding,
                            center + blackDiv2 - padding,
                            center + blackDiv2 - padding,
                            center - blackDiv2 + padding,
                    },
                    new double[]{
                            y,
                            y,
                            y + blackKeyHeight - padding,
                            y + blackKeyHeight - padding,
                    },
                    4);
            default:
                throw new IllegalStateException("Unexpected value: " + note);
        }
    }
}
