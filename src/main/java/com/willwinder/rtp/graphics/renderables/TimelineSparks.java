package com.willwinder.rtp.graphics.renderables;

import com.willwinder.rtp.graphics.Renderable;
import com.willwinder.rtp.model.KeyboardState;
import com.willwinder.rtp.model.TimelineNotes;
import com.willwinder.rtp.model.params.TimelineParams;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.time.Duration;

/**
 * Render sparks in the timeline to represent pressed keys.
 */
public class TimelineSparks implements Renderable {
    private static Duration CLEANUP_INTERVAL = Duration.ofSeconds(5);

    private final TimelineParams params;
    private KeyboardState state;

    /**
     * Create the timeline spark renderable.
     * @param params
     */
    public TimelineSparks(TimelineParams params, KeyboardState state) {
        this.params = params;
        this.state = state;
    }

    /**
     * Draw the sparks.
     * @param gc the GraphicsContext where the drawing should be made.
     * @param p the params object full of useful bits and bobs for the Renderables.
     * @throws RenderableException
     */
    @Override
    public void draw(GraphicsContext gc, DrawParams p) throws RenderableException {
        long topMs = 0;
        long duration = this.params.timelineDurationMs.get();
        if(this.params.out.get()) {
            topMs = p.nowMs - duration;
        } else {
            topMs = p.nowMs + duration;
        }

        // Process the sparks
        synchronized (params.midiNotes) {
            for (var note : this.params.midiNotes) {
                long ageCutoff = this.params.out.get() ? topMs : p.nowMs;

                // TODO: Who is responsiblge for clearing out old stuff now?
                //       There is an EndOfTrack event, perhaps that could help somehow.
                // If it's too old, remove it.
                if (note.endTimeMs > 0 && note.endTimeMs < ageCutoff) {
                    //iter.remove();
                    continue;
                }

                // Draw it if the start time is before the cutoff.
                if (note.endTimeMs < 0 || note.endTimeMs > ageCutoff) {
                    drawSpark(gc, note, topMs, p.canvasWidth, p.canvasHeight, p.nowMs);
                }
            }
        }

        // Process the sparks
        synchronized (params.playerNotes) {
            for (var noteList : this.params.playerNotes) {
                for (var note : noteList) {
                    long ageCutoff = this.params.out.get() ? topMs : p.nowMs;

                    // TODO: Who is responsiblge for clearing out old stuff now?
                    //       There is an EndOfTrack event, perhaps that could help somehow.
                    // If it's too old, remove it.
                    if (note.endTimeMs > 0 && note.endTimeMs < ageCutoff) {
                        //iter.remove();
                        continue;
                    }

                    // Draw it if the start time is before the cutoff.
                    if (note.endTimeMs < 0 || note.endTimeMs > ageCutoff) {
                        drawSpark(gc, note, topMs, p.canvasWidth, p.canvasHeight, p.nowMs);
                    }
                }
            }
        }
    }

    /**
     * Compute the spark size, and then draw it.
     * @param gc GraphicsContext where the spark will be drawn.
     * @param note metadata associated with the spark.
     * @param topMs timestamp in milliseconds of the top of the timeline. "now" is at the bottom.
     * @param w width of the timeline canvas.
     * @param h height of the timeline canvas.
     * @param now precomputed now timestamp to ensure all sparks are aligned.
     */
    private void drawSpark(GraphicsContext gc, TimelineNotes.TimelineNote note, long topMs, double w, double h, long now) {
        // Get X dimensions (key width)
        var points = this.params.keyPointCache.getPoints(note.key.key);

        // TODO: Handle unknown keys
        if (points == null) return;

        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        for (double p : points.xPoints) {
            if (p < xMin) xMin = p;
            if (p > xMax) xMax = p;
        }

        // Get Y dimensions (key press timestamps)
        double timelineHeight = h - params.keyPointCache.getWhiteKeyHeight();
        long duration = this.params.timelineDurationMs.get();

        double yMax = 0.0;
        double yMin = 0.0;

        boolean isNoteActive = false;
        // Notes are outgoing
        if (this.params.out.get()) {
            if (note.endTimeMs > 0) {
                long range = note.endTimeMs - topMs;
                yMax = range / (double) duration * timelineHeight;
            } else {
                yMax = timelineHeight;
            }

            if (note.startTimeMs > topMs) {
                long range = note.startTimeMs - topMs;
                yMin = range / (double) duration * timelineHeight;
            } else {
                yMin = 0;
            }
        }
        // Notes are incoming
        else {
            if (note.endTimeMs > 0) {
                long range = topMs - note.endTimeMs;
                yMax = range / (double) duration * timelineHeight;
            } else {
                // If there is no end time, this note is probably from the keyboard. No need to show it.
                return;
                //yMax = timelineHeight;
            }

            if (note.startTimeMs > now) {
                long range = topMs - note.startTimeMs;
                yMin = range / (double) duration * timelineHeight;
            } else {
                isNoteActive = true;
                yMin = timelineHeight;
            }
        }

        boolean isKeyActive = state.getActiveKeyCodes().contains(note.key.key);
        boolean notePressed = isNoteActive && isKeyActive;

        Color fillColor = switch(note.track) {
            case 1 -> notePressed ? Color.ORANGE : Color.YELLOW;
            case 2 -> notePressed ? Color.VIOLET : Color.BLUE;
            case 99 -> Color.RED;
            default -> Color.ORANGE;
        };
        gc.setFill(fillColor);

        gc.fillRect(
                xMin,
                yMin,
                Math.min(xMax - xMin, w),
                Math.min(yMax - yMin, h - params.keyPointCache.getWhiteKeyHeight()));
    }
}
