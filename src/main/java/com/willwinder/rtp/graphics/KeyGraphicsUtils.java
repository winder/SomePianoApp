package com.willwinder.rtp.graphics;

import com.willwinder.rtp.model.Key;

public class KeyGraphicsUtils {
    public record KeyPoints(double[] xPoints, double[] yPoints, int numPoints){}

    private static double BLACK_KEY_HEIGHT_FACTOR = 0.66;

    public static KeyPoints pointsForKey(Key.Note note, double center, double y, double h, double whiteKeyWidth, double blackKeyWidth, double padding) {
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
                            y + h * BLACK_KEY_HEIGHT_FACTOR + padding,
                            y + h * BLACK_KEY_HEIGHT_FACTOR + padding,
                            y + h,
                            y + h
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
                            y + h,
                            y + h,
                            y + h * BLACK_KEY_HEIGHT_FACTOR + padding,
                            y + h * BLACK_KEY_HEIGHT_FACTOR + padding,
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
                            y + h * BLACK_KEY_HEIGHT_FACTOR + padding,
                            y + h * BLACK_KEY_HEIGHT_FACTOR + padding,
                            y + h,
                            y + h,
                            y + h * BLACK_KEY_HEIGHT_FACTOR + padding,
                            y + h * BLACK_KEY_HEIGHT_FACTOR + padding
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
                            y + h * BLACK_KEY_HEIGHT_FACTOR - padding,
                            y + h * BLACK_KEY_HEIGHT_FACTOR - padding,
                    },
                    4);
        };
    }
}
