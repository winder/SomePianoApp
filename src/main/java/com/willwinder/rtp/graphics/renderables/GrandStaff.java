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
        double y = 0;
        this.staffHeight = (this.grandStaffParams.heightPct.get() * params.canvasHeight - this.grandStaffParams.margin.get()) / (double) this.grandStaffParams.rows.get();
        if (this.grandStaffParams.top.get()) {
            y = this.grandStaffParams.margin.get();
        } else {
            y = params.canvasHeight
                    - this.timelineParams.keyPointCache.getWhiteKeyHeight()
                    - this.grandStaffParams.margin.get()
                    - this.staffHeight * this.grandStaffParams.rows.get();
        }
        double imageHeight = 0.75 * staffHeight;
        double imageYOffset = y + ((staffHeight - imageHeight) / 2.5);

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
        for (int i = 0; i < this.grandStaffParams.rows.get(); i++) {
            var offset = i * staffHeight;
            gc.fillRect(x, y + offset,
                    params.canvasWidth, staffHeight);

            gc.drawImage(brace,
                    0, imageYOffset + offset,
                    brace.getWidth(), brace.getHeight());

            gc.drawImage(cleffs,
                    brace.getWidth(), imageYOffset + offset,
                    cleffs.getWidth(), cleffs.getHeight());

            gc.drawImage(bars,
                    brace.getWidth(), imageYOffset + offset,
                    bars.getWidth(), bars.getHeight());
        }

        // Variables for measure lines + notes
        long duration = (long) (this.timelineParams.timelineDurationMs.get() / (double) this.grandStaffParams.rows.get());
        double notesLeftMargin = this.brace.getWidth() + 2 * this.cleffs.getWidth();

        int rows = this.grandStaffParams.rows.get();
        for (int i = 0; i < rows; i++) {
            double yOffset = this.grandStaffParams.descending.get() ? y + i * staffHeight : y + (rows - i - 1) * staffHeight;
            long durOffset = duration * i;
            long timelineEndMs   = this.timelineParams.out.get() ? params.nowMs + durOffset            : params.nowMs + duration + durOffset;
            long timelineStartMs = this.timelineParams.out.get() ? params.nowMs + duration + durOffset : params.nowMs + durOffset;
            ///////////////////
            // Measure lines // disabled in realtime mode
            ///////////////////
            if (!this.timelineParams.out.get()) {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(4);

                double measureDuration = this.timelineParams.measureDurationMs.get();
                double lastMeasure = timelineEndMs / (long) measureDuration * measureDuration;
                double y1 = getYOffsetForNote(5, Key.Note.F, Cleff.TREBLE, staffHeight) + yOffset;
                double y2 = getYOffsetForNote(2, Key.Note.G, Cleff.BASS, staffHeight) + yOffset;
                while (lastMeasure > timelineStartMs) {
                    double xOffsetFactor = (lastMeasure - timelineStartMs) / (double) duration;
                    double xOffset = notesLeftMargin + xOffsetFactor * (params.canvasWidth - notesLeftMargin);
                    gc.strokeLine(xOffset, y1, xOffset, y2);
                    lastMeasure -= measureDuration;
                }
            }

            ///////////
            // Notes //
            ///////////

            // Process the sparks
            boolean realtime = this.timelineParams.out.get();
            synchronized (timelineParams.midiNotes) {
                for (var note : this.timelineParams.midiNotes) {
                    if (note.startTimeMs < timelineEndMs && ((note.endTimeMs < 0) || (note.endTimeMs > timelineStartMs))) {
                        drawNote(gc, note, timelineEndMs, duration, notesLeftMargin, params.canvasWidth - notesLeftMargin, yOffset, this.staffHeight, realtime);
                    }
                }
            }

            synchronized (timelineParams.playerNotes) {
                for (var noteList : this.timelineParams.playerNotes) {
                    for (var note : noteList) {
                        if (note.startTimeMs < timelineEndMs && ((note.endTimeMs < 0) || (note.endTimeMs > timelineStartMs))) {
                            drawNote(gc, note, timelineEndMs, duration, notesLeftMargin, params.canvasWidth - notesLeftMargin, yOffset, this.staffHeight, realtime);
                        }
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
    static private double getYOffsetForNote(int octave, Key.Note note, Cleff cleff, double staffHeight) {
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

        return staffHeight * (octaveMultiplier + cleffMultiplierOffset);
    }

    static private Cleff getCleff(Key key) {
        if (key.octave < 4) return Cleff.BASS;
        if (key.octave > 4) return Cleff.TREBLE;
        return switch (key.note) {
            case A, A_SHARP, B, C, C_SHARP -> Cleff.BASS;
            default -> Cleff.TREBLE;
        };
    }

    // TODO: Convert to 'drawMeasure' and accept a series of notes so that a bar can be drawn over notes when necessary.
    //       Computing the measure when creating the Key object is probably going to be a good idea.
    private static void drawNote(GraphicsContext gc, TimelineNotes.TimelineNote note, long timelineStartMs, long durationMs, double barsLeftMargin, double barsWidth, double areaYOffset, double staffHeight, boolean realtime) {
        // Notes need to scroll left to right.
        // Lets start by drawing the note at the correct height by drawing it right in the middle.
        Cleff c = getCleff(note.key);

        if (note.track == 1) {
            c = Cleff.TREBLE;
        }
        if (note.track == 2) {
            c = Cleff.BASS;
        }

        double yOffset = getYOffsetForNote(note.key.octave, note.key.note, c, staffHeight) + areaYOffset;
        double xOffsetStart, xOffsetEnd;
        double height = 15;
        double width = 20;

        double h;
        double w;

        // Note: Using max here means the note head will crash into the edge and remain visible until the note ends.
        double startOffsetFactor = Math.max(1 - ((timelineStartMs - note.startTimeMs) / (double) durationMs), 0.0);
        double endOffsetFactor   = Math.max(1 - ((timelineStartMs - note.endTimeMs)   / (double) durationMs), 0.0);

        // Tweak factors for realtime mode.
        if (realtime) {
            if (note.endTimeMs < 0) {
                endOffsetFactor = 0;
            } else {
                endOffsetFactor   = 1 - endOffsetFactor;
            }
            startOffsetFactor = 1 - startOffsetFactor;
        }
        xOffsetStart = barsLeftMargin + startOffsetFactor * barsWidth;
        xOffsetEnd   = barsLeftMargin + endOffsetFactor   * barsWidth;

        // Draw the note tail
        if (xOffsetStart < (barsWidth + barsLeftMargin)) {
            gc.setFill(Color.RED);
            // Rectangle
            //gc.fillRect(xOffsetStart, yOffset - 2,
            //        xOffsetEnd - xOffsetStart, 4);
            // Triangle
            var xpts = new double[]{xOffsetStart, xOffsetEnd, xOffsetStart};
            var ypts = new double[]{yOffset - height/4.0, yOffset, yOffset + height/4.0};
            gc.fillPolygon(xpts,  ypts, 3);
        }

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
