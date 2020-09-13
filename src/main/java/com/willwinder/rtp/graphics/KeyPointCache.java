package com.willwinder.rtp.graphics;

import com.willwinder.rtp.model.Key;

import java.util.HashMap;

public class KeyPointCache {
    private final HashMap<Integer, KeyPointCache.KeyPoints> cache = new HashMap<>();

    private double height;
    private double width;

    private double leftMargin;
    private double rightMargin;
    private double whiteKeyHeight;
    private double whiteKeyWidth;
    private double blackKeyHeight;
    private double blackKeyRatio;
    private double blackKeyWidth;
    private double blackKeyHeightRatio;
    private double whiteKeyHeightRatio;
    private double padding;
    public int firstKey;
    public int numKeys;

    /**
     * @param height height of the canvas to allow positioning keys at the bottom.
     * @param width width of the canvas to compute the key scale.
     * @param leftMargin margin to the left of the keys.
     * @param rightMargin margin to the right of the keys
     * @param blackKeyWidthRatio ratio between white key width and black key width.
     * @param blackKeyHeightRatio ratio between white key height and black key height.
     * @param whiteKeyHeightRatio ratio from white key width to white key height.
     * @param padding padding around keys, removed from the size, not added.
     * @param firstKey the first key on keyboard, MIDI key code (A0 = 21).
     * @param numKeys number of keys on keyboard.
     */
    public KeyPointCache(
            double height,
            double width,
            double leftMargin,
            double rightMargin,
            double blackKeyWidthRatio,
            double blackKeyHeightRatio,
            double whiteKeyHeightRatio,
            double padding,
            int firstKey,
            int numKeys) {
        this.height = height;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.blackKeyHeightRatio = blackKeyHeightRatio;
        this.whiteKeyHeightRatio = whiteKeyHeightRatio;
        this.blackKeyRatio = blackKeyWidthRatio;
        this.padding = padding;
        this.firstKey = firstKey;
        this.numKeys = numKeys;

        reset(height, width);
    }

    public double getBlackKeyHeight() {
        return blackKeyHeight;
    }

    public double getBlackKeyWidth() {
        return blackKeyWidth;
    }

    public double getWhiteKeyHeight() {
        return whiteKeyHeight;
    }

    public double getWhiteKeyWidth() {
        return whiteKeyWidth;
    }

    public void reset(double newHeight, double newWidth) {
        // If nothing changed and this isn't the first call to reset, this is a no-op.
        if (newWidth == width &&  newHeight == height && this.cache.size() != 0) return;

        this.height = newHeight;
        this.width = newWidth;
        cache.clear();

        // Compute whiteKeyWidth
        Key.Note note = Key.Note.noteForKey(firstKey);
        int numWhiteKeys = 0;
        for (int i = 0; i < numKeys; i++) {
            if (note.isWhiteKey()) {
                numWhiteKeys++;
            }
            note = note.nextNote();
        }

        whiteKeyWidth = (width - leftMargin - rightMargin) / (double) numWhiteKeys;
        blackKeyWidth = whiteKeyWidth * blackKeyRatio;
        whiteKeyHeight = whiteKeyWidth * whiteKeyHeightRatio;
        blackKeyHeight = whiteKeyHeight * blackKeyHeightRatio;

        note = Key.Note.noteForKey(this.firstKey);
        var xStep = getWhiteKeyWidth() / 2.0;
        var xCenter = leftMargin + xStep;
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
