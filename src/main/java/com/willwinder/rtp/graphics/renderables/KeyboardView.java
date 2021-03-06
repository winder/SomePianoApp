package com.willwinder.rtp.graphics.renderables;

import com.google.common.eventbus.Subscribe;
import com.willwinder.rtp.model.KeyboardState;
import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.graphics.Renderable;
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

    private final Set<Integer> updatedKeys = new HashSet<>();
    private final KeyboardState state;
    private final KeyPointCache keyPointCache;

    public KeyboardView(KeyboardState state, KeyPointCache cache) {
        this.state = state;
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
    }

    @Subscribe
    public void noteEventHandler(NoteEvent event) {
        synchronized (updatedKeys) {
            updatedKeys.add(event.key.key);
        }
    }

    /**
     * Draw the keyboard.
     * @param gc GraphicsContext to use for drawing.
     * @param p the params object full of useful bits and bobs for the Renderables.
     */
    @Override
    public void draw(GraphicsContext gc, DrawParams p) {
        if (p.reset) {
            this.keyPointCache.reset(p.canvasHeight, p.canvasWidth);

            // reset all keys
            Key.Note note = Key.Note.noteForKey(this.keyPointCache.params.firstKey.get());
            for (int keyOffset = 0; keyOffset < this.keyPointCache.params.numKeys.get(); keyOffset++) {
                int keyNum = keyOffset + this.keyPointCache.params.firstKey.get();
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
