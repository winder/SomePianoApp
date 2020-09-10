package com.willwinder.rtp.graphics;

import com.willwinder.rtp.KeyboardState;
import com.willwinder.rtp.model.Key;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Set;

/**
 * KeyboardView is used to draw the keyboard to a JavaFX canvas. It contains a reference
 * to the keyboard state and can render the  keys according to their active state.
 */
public class KeyboardView implements Renderable {
    public record KeyboardViewParams(
            double x,
            double y,
            boolean bottom,
            double blackKeyWidth,
            double blackKeyHeight,
            double whiteKeyWidth,
            double whiteKeyHeight,
            int numOctaves,
            int firstOctave,
            double padding
    ) {}

    private final KeyboardState state;
    private final KeyboardViewParams params;
    private double whiteKeyWidthDiv2;
    private double currentScale = 0.0;
    private HashMap<Integer, KeyPoints> keyPointCache = new HashMap<>();

    public KeyboardView(KeyboardState state, KeyboardViewParams params) {
        this.state = state;
        this.params = params;
        this.whiteKeyWidthDiv2 = params.whiteKeyWidth / 2.0;
    }

    /**
     * Draw a single key at the requested location.
     *
     * @param gc GraphicsContext to use for drawing.
     * @param note the note to draw.
     * @param xCenter x center line where the note will be drawn.
     * @param y y offset where the note will be drawn.
     * @return
     */
    private double drawKey(GraphicsContext gc, int keyNum, Key.Note note, boolean isActive, double xCenter, double y, double scale) {
        Color c;
        if (isActive) {
            //c = new Color(Math.random(), Math.random(), Math.random(), 1.0);
            c = Color.GREEN;
        } else if (note.isWhiteKey()) {
            c = Color.WHITE;
        } else {
            c = Color.GRAY;

        }

        gc.setFill(c);

        // warm the cache
        var kp = this.keyPointCache.computeIfAbsent(keyNum, key ->
                pointsForKey(note, xCenter, y,
                    params.whiteKeyHeight * scale,
                    params.whiteKeyWidth * scale,
                    params.blackKeyHeight * scale,
                    params.blackKeyWidth * scale,
                    params.padding));


        /*
        var kp = pointsForKey(note, xCenter, y,
                params.whiteKeyHeight * scale,
                params.whiteKeyWidth * scale,
                params.blackKeyHeight * scale,
                params.blackKeyWidth * scale,
                //params.padding * scale);
                params.padding);
         */
        gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());

        // If there is no sharp, move over 2 keys.
        var offsets = note.nextNoteIntervalIsSemitone(Key.Note.C) ? 1 : 2;
        return xCenter + offsets * whiteKeyWidthDiv2;
    }

    /**
     * Draw a single octave and return the offset of the next one.
     * @return offset that the next octave would use for the x position.
     * @throws RenderableException
     */
    private double drawOctave(GraphicsContext gc, int octave, double x, double y, double scale) throws RenderableException {
        var offset = x;

        for (Key.Note note : Key.Note.values()) {
            int keyNum = (octave - 1) * 12 + note.keyIndex();
            boolean isActive = state.getActiveKeyCodes().contains(keyNum);
            offset = drawKey(gc, keyNum, note, isActive, offset, y, scale);
        }

        return offset;
    }

    /**
     * Draw the keyboard.
     *
     * @param gc GraphicsContext to use for drawing.
     * @param scale the desired scale of the window.
     * @throws RenderableException
     */
    @Override
    public void draw(GraphicsContext gc, double scale) throws RenderableException {
        if (scale != this.currentScale) {
            this.currentScale = scale;
            this.keyPointCache.clear();
        }
        this.whiteKeyWidthDiv2 = params.whiteKeyWidth * scale / 2.0;
        var offset = params.x;

        var yOffset = params.y;
        if (params.bottom) {
            var h = gc.getCanvas().getHeight();
            yOffset = h - (params.whiteKeyHeight * scale);
        }

        for (int i = 0; i < params.numOctaves; i++) {
            offset = drawOctave(gc, params.firstOctave + i, offset, yOffset, scale);
        }
    }

    /**
     * Internal response tuple for the polygon drawing computation.
     */
    private record KeyPoints(double[] xPoints, double[] yPoints, int numPoints){}

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
