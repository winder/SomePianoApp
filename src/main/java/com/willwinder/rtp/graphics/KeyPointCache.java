package com.willwinder.rtp.graphics;

import com.willwinder.rtp.model.Key;

import java.util.HashMap;

public class KeyPointCache {
    private final HashMap<Integer, KeyPointCache.KeyPoints> cache = new HashMap<>();

    private double height;
    private double x;
    private double whiteKeyHeight;
    private double whiteKeyWidth;
    private double blackKeyHeight;
    private double blackKeyWidth;
    private double padding;
    public int firstKey;
    public int numKeys;
    private double scale;

    public KeyPointCache(
            double height,
            double x,
            double blackKeyWidth,
            double blackKeyHeight,
            double whiteKeyWidth,
            double whiteKeyHeight,
            double padding,
            double scale,
            int firstKey,
            int numKeys) {
        this.height = height;
        this.x = x;
        this.whiteKeyHeight = whiteKeyHeight;
        this.whiteKeyWidth = whiteKeyWidth;
        this.blackKeyHeight = blackKeyHeight;
        this.blackKeyWidth = blackKeyWidth;
        this.padding = padding;
        this.firstKey = firstKey;
        this.numKeys = numKeys;
        this.scale = scale;

        reset(height, scale);
    }

    public double getBlackKeyHeight() {
        return blackKeyHeight * scale;
    }

    public double getBlackKeyWidth() {
        return blackKeyWidth * scale;
    }

    public double getWhiteKeyHeight() {
        return whiteKeyHeight * scale;
    }

    public double getWhiteKeyWidth() {
        return whiteKeyWidth * scale;
    }

    public void reset(double newHeight, double newScale) {
        // If nothing changed and this isn't the first call to reset, this is a no-op.
        if (newScale == scale &&  newHeight == height && this.cache.size() != 0) return;

        this.scale = newScale;
        this.height = newHeight;
        cache.clear();

        Key.Note note = Key.Note.noteForKey(this.firstKey);
        var xStep = getWhiteKeyWidth() / 2.0;
        var xCenter = x + xStep;
        for (int i = this.firstKey; i < this.firstKey + numKeys; i++) {
            this.cache.put(i, pointsForKey(
                    note,
                    xCenter,
                    height - getWhiteKeyHeight(),
                    getWhiteKeyHeight(),
                    getWhiteKeyWidth(),
                    getBlackKeyHeight(),
                    getBlackKeyWidth(),
                    padding));
            xCenter += xStep;
            // Add another step if there is no sharp.
            if (!note.nextNoteIntervalIsSemitone(Key.Note.C)) {
                xCenter += xStep;
            }
            note = note.nextNote();
        }
    }

    // TODO: Figure out a way to compute xCenter
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

        return switch(note) {
            case C, F -> new KeyPoints(
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
            case E, B -> new KeyPoints(
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
            case D, G, A ->new KeyPoints(
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
            case C_SHARP, D_SHARP, F_SHARP, G_SHARP, A_SHARP -> new KeyPoints(
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
        };
    }
}
