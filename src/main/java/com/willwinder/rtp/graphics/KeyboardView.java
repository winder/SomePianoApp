package com.willwinder.rtp.graphics;

import com.google.common.eventbus.Subscribe;
import com.willwinder.rtp.KeyboardState;
import com.willwinder.rtp.model.Key;
import com.willwinder.rtp.util.NoteEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.Set;

/**
 * KeyboardView is used to draw the keyboard to a JavaFX canvas. It contains a reference
 * to the keyboard state and can render the  keys according to their active state.
 */
public class KeyboardView implements Renderable {

    public static class KeyboardViewParams {
        final double x;
        final double y;
        final boolean bottom;
        final int numOctaves;
        final int firstOctave;
        final double padding;

        public KeyboardViewParams(double x, double y, boolean bottom, int numOctaves, int firstOctave, double padding) {
            this.x = x;
            this.y = y;
            this.bottom = bottom;
            this.numOctaves = numOctaves;
            this.firstOctave = firstOctave;
            this.padding = padding;
        }
    }

    private final Set<Integer> updatedKeys = new HashSet<>();
    private final KeyboardState state;
    private final KeyboardViewParams params;
    private double currentScale = 0.0;
    private final KeyPointCache keyPointCache;

    public KeyboardView(KeyboardState state, KeyboardViewParams params, KeyPointCache cache) {
        this.state = state;
        this.params = params;
        this.keyPointCache = cache;
    }

    /**
     * Draw a single key at the cached location.
     *
     * @param gc GraphicsContext to use for drawing.
     * @param note the note to draw.
     */
    private void drawKey(GraphicsContext gc, int keyNum, Key.Note note, boolean isActive) {
        Color c;
        if (isActive) {
            c = new Color(Math.random(), Math.random(), Math.random(), 1.0);
            //c = Color.GREEN;
        } else if (note.isWhiteKey()) {
            c = Color.WHITE;
        } else {
            c = Color.GRAY;

        }

        gc.setFill(c);

        var kp = this.keyPointCache.getPoints(keyNum);

        // TODO: Don't ignore missing keys.
        // Ignore missing keys
        if (kp != null) {
            gc.fillPolygon(kp.xPoints, kp.yPoints, kp.numPoints);
        }

        // If there is no sharp, move over 2 keys.
        var offsets = note.nextNoteIntervalIsSemitone(Key.Note.C) ? 1 : 2;
    }

    @Subscribe
    public void noteEventHandler(NoteEvent event) {
        synchronized (updatedKeys) {
            updatedKeys.add(event.key.key);
        }
    }

    /**
     * Draw the keyboard.
     *
     * @param gc GraphicsContext to use for drawing.
     * @param reset indicates that the display has been reset and a full redraw should occur.
     * @param scale the desired scale of the window.
     */
    @Override
    public void draw(GraphicsContext gc, boolean reset, double scale) {
        if (reset || scale != this.currentScale) {
            this.currentScale = scale;
            this.keyPointCache.reset(gc.getCanvas().getHeight(), scale);

            // reset all keys
            Key.Note note = Key.Note.noteForKey(this.keyPointCache.firstKey);
            for (int keyOffset = 0; keyOffset < this.keyPointCache.numKeys; keyOffset++) {
                int keyNum = keyOffset + this.keyPointCache.firstKey;
                boolean isActive = state.getActiveKeyCodes().contains(keyNum);
                drawKey(gc, keyNum, note, isActive);
                note = note.nextNote();
            }
        }
        else {
            synchronized (updatedKeys) {
                for (int keyNum : updatedKeys) {
                    Key.Note note = Key.Note.noteForKey(keyNum);
                    boolean isActive = state.getActiveKeyCodes().contains(keyNum);
                    drawKey(gc, keyNum, note, isActive);
                }
                updatedKeys.clear();
            }
        }
    }
}
