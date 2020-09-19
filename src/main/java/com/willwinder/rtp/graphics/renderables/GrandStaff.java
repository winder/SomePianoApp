package com.willwinder.rtp.graphics.renderables;

import com.willwinder.rtp.graphics.Renderable;
import com.willwinder.rtp.model.Key;
import com.willwinder.rtp.model.TimelineNotes;
import com.willwinder.rtp.model.params.GrandStaffParams;
import com.willwinder.rtp.model.params.TimelineParams;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.ListIterator;

public class GrandStaff implements Renderable {

    private TimelineParams timelineParams;
    private final GrandStaffParams grandStaffParams;

    private Image brace = null;
    private Image cleffs = null;
    private Image bars = null;
    private double staffHeight = 0.0;

    public GrandStaff(TimelineParams timelineParams, GrandStaffParams grandStaffParams) {
        this.timelineParams = timelineParams;
        this.grandStaffParams = grandStaffParams;
    }

    @Override
    public void draw(GraphicsContext gc, DrawParams params) throws RenderableException {
        double x = 0;
        double y = this.grandStaffParams.topMargin.get();
        this.staffHeight = this.grandStaffParams.heightPct.get() * params.canvasHeight - this.grandStaffParams.topMargin.get();
        double imageHeight = 0.75 * staffHeight;
        double imageYOffset = this.grandStaffParams.topMargin.get() + ((staffHeight - imageHeight) / 2.5);

        // Scale the images when needed.
        if (params.reset || this.brace == null || this.cleffs == null || this.bars == null) {
            this.brace = getBrace(imageHeight);
            this.cleffs = getCleffs(imageHeight);
            this.bars = getBars(imageHeight, params.canvasWidth - this.brace.getWidth());
            this.grandStaffParams.leftNoteMargin.set(imageYOffset + this.cleffs.getWidth() * 2);
        }

        // not applicable to this mode, return after setting the images so they don't get out of date.
        //if (timelineParams.out.get()) return;

        ////////////////
        // Background //
        ////////////////
        gc.setFill(Color.ANTIQUEWHITE);
        gc.fillRect(x, y,
                params.canvasWidth, staffHeight);

        gc.drawImage(brace,
                0, imageYOffset,
                brace.getWidth(),  brace.getHeight());

        gc.drawImage(cleffs,
                brace.getWidth(), imageYOffset,
                cleffs.getWidth(), cleffs.getHeight());

        gc.drawImage(bars,
                brace.getWidth(), imageYOffset,
                bars.getWidth(), bars.getHeight());

        ///////////
        // Notes //
        ///////////
        long duration = this.timelineParams.timelineDurationMs.get();
        long timelineEndMs   = this.timelineParams.out.get() ? params.nowMs            : params.nowMs + duration;
        long timelineStartMs = this.timelineParams.out.get() ? params.nowMs - duration : params.nowMs;

        double notesLeftMargin = this.brace.getWidth() + 2 * this.cleffs.getWidth();

        // Process the sparks
        synchronized (timelineParams.timelineNotes) {
            for (var sparkList : this.timelineParams.timelineNotes) {
                ListIterator<TimelineNotes.TimelineNote> iter = sparkList.listIterator();
                while (iter.hasNext()) {
                    var note = iter.next();

                    if (note.startTimeMs < timelineEndMs && ((note.endTimeMs <0) || (note.endTimeMs > timelineStartMs))) {
                        drawNote(gc, note, timelineEndMs, timelineStartMs, notesLeftMargin, params.canvasWidth - notesLeftMargin, params.canvasHeight);
                    }
                }
            }
        }
    }

    private Image getBrace(double height) {
        String imagePath = "/images/grand_staff_brace.png";
        InputStream is = getClass().getResourceAsStream(imagePath);
        return new Image(is, 0, height, true, true);
    }

    private Image getCleffs(double height) {
        String imagePath = "/images/grand_staff_cleffs.png";
        InputStream is = getClass().getResourceAsStream(imagePath);
        return new Image(is, 0, height, true, true);
    }

    private Image getBars(double height, double width) {
        String imagePath = "/images/bars.png";
        InputStream is = getClass().getResourceAsStream(imagePath);
        return new Image(is, width, height, false, false);
    }

    private enum Cleff {
        TREBLE,
        BASS
    }

    /**
     * Compute the offset into the canvas.
     * @param octave
     * @param note
     * @param cleff
     * @return
     */
    private double getYOffsetForNote(int octave, Key.Note note, Cleff cleff) {
        double cleffMultiplierOffset = switch(cleff) {
            case TREBLE -> 0.0;
            case BASS   -> 0.162;
        };

        // I hate this, but the staff I built is not perfect so fine tuning the offset is required.
        double octaveMultiplier = 0.0;
        if (octave == 8) {
            octaveMultiplier = switch(note) {
                case G, G_SHARP -> -0.381;
                case F, F_SHARP -> -0.356;
                case E          -> -0.331;
                case D, D_SHARP -> -0.306;
                case C, C_SHARP -> -0.281;
                case B          -> -0.256;
                case A, A_SHARP -> -0.231;
            };
        }
        if (octave == 7) {
            octaveMultiplier = switch (note) {
                case G, G_SHARP -> -0.206;
                case F, F_SHARP -> -0.181;
                case E -> -0.156;
                case D, D_SHARP -> -0.131;
                case C, C_SHARP -> -0.106;
                case B -> -0.081;
                case A, A_SHARP -> -0.056;
            };
        }
        if (octave == 6) {
            octaveMultiplier = switch(note) {
                case G, G_SHARP -> -0.031;
                case F, F_SHARP -> -0.006;
                case E          -> 0.019;
                case D, D_SHARP -> 0.044;
                case C, C_SHARP -> 0.069;
                case B          -> 0.094;
                case A, A_SHARP -> 0.119;
            };
        }
        if (octave == 5) {
            octaveMultiplier = switch(note) {
                case G, G_SHARP -> 0.144;
                case F, F_SHARP -> 0.169;
                case E          -> 0.194;
                case D, D_SHARP -> 0.22;
                case C, C_SHARP -> 0.245;
                case B          -> 0.27;
                case A, A_SHARP -> 0.297;
            };
        }
        if (octave == 4) {
            octaveMultiplier = switch(note) {
                case G, G_SHARP -> 0.325;
                case F, F_SHARP -> 0.35;
                case E          -> 0.375;
                case D, D_SHARP -> 0.4;
                case C, C_SHARP -> 0.425;
                case B          -> 0.45;
                case A, A_SHARP -> 0.475;
            };
        }
        if (octave == 3) {
            octaveMultiplier = switch(note) {
                case G, G_SHARP -> 0.5;
                case F, F_SHARP -> 0.525;
                case E          -> 0.553;
                case D, D_SHARP -> 0.578;
                case C, C_SHARP -> 0.606;
                case B          -> 0.631;
                case A, A_SHARP -> 0.656;
            };
        }
        if (octave == 2) {
            octaveMultiplier = switch(note) {
                case G, G_SHARP -> 0.681;
                case F, F_SHARP -> 0.706;
                case E          -> 0.731;
                case D, D_SHARP -> 0.756;
                case C, C_SHARP -> 0.781;
                case B          -> 0.806;
                case A, A_SHARP -> 0.831;
            };
        }
        if (octave == 1) {
            octaveMultiplier = switch(note) {
                case G, G_SHARP -> 0.856;
                case F, F_SHARP -> 0.881;
                case E          -> 0.906;
                case D, D_SHARP -> 0.931;
                case C, C_SHARP -> 0.956;
                case B          -> 0.981;
                case A, A_SHARP -> 1.006;
            };
        }
        if (octave == 0) {
            octaveMultiplier = switch(note) {
                case G, G_SHARP -> 1.031;
                case F, F_SHARP -> 1.056;
                case E          -> 1.081;
                case D, D_SHARP -> 1.106;
                case C, C_SHARP -> 1.131;
                case B          -> 1.156;
                case A, A_SHARP -> 1.181;
            };
        }
        if (octave == -1) {
            octaveMultiplier = switch(note) {
                case G, G_SHARP -> 1.206;
                case F, F_SHARP -> 1.231;
                case E          -> 1.256;
                case D, D_SHARP -> 1.281;
                case C, C_SHARP -> 1.306;
                case B          -> 1.331;
                case A, A_SHARP -> 1.356;
            };
        }

        return this.staffHeight * (octaveMultiplier + cleffMultiplierOffset);
    }

    private Cleff getCleff(Key key) {
        if (key.octave < 4) return Cleff.BASS;
        if (key.octave > 4) return Cleff.TREBLE;
        return switch (key.note) {
            case A, A_SHARP, B, C, C_SHARP -> Cleff.BASS;
            default -> Cleff.TREBLE;
        };
    }

    private void drawNote(GraphicsContext gc, TimelineNotes.TimelineNote note, long timelineStartMs, long timelineEndMs, double barsLeftMargin, double barsWidth, double canvasHeight) {
        // Notes need to scroll left to right.
        // Lets start by drawing the note at the correct height by drawing it right in the middle.
        Cleff c = getCleff(note.key);

        if (note.track == 1) {
            c = Cleff.TREBLE;
        }
        if (note.track == 2) {
            c = Cleff.BASS;
        }

        double yOffset = this.getYOffsetForNote(note.key.octave, note.key.note, c) + this.grandStaffParams.topMargin.get();
        double xOffsetStart, xOffsetEnd;
        double height = 15;
        double width = 20;

        int duration = this.timelineParams.timelineDurationMs.get();
        double h;
        double w;

        // Note: Using max here means the note head will crash into the edge and remain visible until the note ends.
        double startOffsetFactor = Math.max(1 - ((timelineStartMs - note.startTimeMs) / (double) duration), 0.0);
        double endOffsetFactor   = Math.max(1 - ((timelineStartMs - note.endTimeMs)   / (double) duration), 0.0);
        if (this.timelineParams.out.get()) {
            startOffsetFactor = 1 - startOffsetFactor;
            endOffsetFactor   = 1 - startOffsetFactor;
        }
        xOffsetStart = barsLeftMargin + startOffsetFactor * barsWidth;
        xOffsetEnd   = barsLeftMargin + endOffsetFactor   * barsWidth;


        // Draw the tail
        if (xOffsetStart < (barsWidth + barsLeftMargin)) {
            gc.setFill(Color.RED);
            // Rectangle
            //gc.fillRect(xOffsetStart, yOffset - 2,
            //        xOffsetEnd - xOffsetStart, 4);
            // Triangle
            var xpts = new double[]{xOffsetStart, xOffsetEnd, xOffsetStart};
            var ypts = new double[]{yOffset - height/2.0, yOffset, yOffset + height/2.0};
            gc.fillPolygon(xpts,  ypts, 3);
        }

        // Draw the note tail

        // Draw the note head.
        if (startOffsetFactor >= 0) {
            // Thin white border
            //h = height+1;
            //w = width+1;
            //gc.setFill(Color.ANTIQUEWHITE);
            //gc.fillOval(xOffset / 2.0 - w / 2.0, this.grandStaffParams.topMargin.get() + yOffset - h/2.0, w, h);

            // Block oval
            h = height;
            w = width;
            gc.setFill(Color.BLACK);
            gc.fillOval(xOffsetStart - w / 2.0, yOffset - h/2.0, w, h);

            // White center
            h = height-3;
            w = width-3;
            gc.setFill(Color.ANTIQUEWHITE);
            gc.fillOval(xOffsetStart - w / 2.0, yOffset - h/2.0, w, h);
        }
    }
}
