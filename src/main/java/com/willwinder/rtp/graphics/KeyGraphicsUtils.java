package com.willwinder.rtp.graphics;

import com.willwinder.rtp.model.Key;

public class KeyGraphicsUtils {
    public record KeyPoints(double[] xPoints, double[] yPoints, int numPoints){}

    private static double BLACK_KEY_HEIGHT_FACTOR = 0.66;
    private static double WHITE_KEY_WIDTH_MULTIPLIER = 1.65;

    /*
    public static KeyPoints pointsForKey(Key.Note note, double center, double y, double h, double blackKeyWidth, double padding) {
        return switch(note) {
            case C, F -> new KeyPoints(
                    new double[]{
                            center - (blackKeyWidth / 2.0) + padding,
                            center + (blackKeyWidth / 2.0) - padding,
                            center + (blackKeyWidth / 2.0) - padding,
                            center + (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER) - (blackKeyWidth / 2.0) - padding,
                            center + (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER) - (blackKeyWidth / 2.0) - padding,
                            center - (blackKeyWidth / 2.0) + padding
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
            case A, G -> new KeyPoints(
                    new double[]{
                            center - (blackKeyWidth / 2.0) + padding,
                            center + (blackKeyWidth / 2.0) - padding,
                            center + (blackKeyWidth / 2.0) - padding,
                            center + (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER / 2.0) - padding,
                            center + (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER / 2.0) - padding,
                            center - (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER / 2.0) + padding,
                            center - (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER / 2.0) + padding,
                            center - (blackKeyWidth / 2.0) + padding
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
            case D -> new KeyPoints(
                    new double[]{
                            center - (blackKeyWidth / 2.0) + padding,
                            center + (blackKeyWidth / 2.0) - padding,
                            center + (blackKeyWidth / 2.0) - padding,
                            center + (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER / 2.0) - padding,
                            center + (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER / 2.0) - padding,
                            center - (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER / 2.0) + padding,
                            center - (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER / 2.0) + padding,
                            center - (blackKeyWidth / 2.0) + padding
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
            case E, B -> new KeyPoints(
                    new double[]{
                            center - (blackKeyWidth / 2.0) + padding,
                            center + (blackKeyWidth / 2.0) - padding,
                            center + (blackKeyWidth / 2.0) - padding,
                            center - (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER) + (blackKeyWidth / 2.0) + padding,
                            center - (blackKeyWidth * WHITE_KEY_WIDTH_MULTIPLIER) + (blackKeyWidth / 2.0) + padding,
                            center - (blackKeyWidth / 2.0) + padding
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
            case C_SHARP, D_SHARP, F_SHARP, G_SHARP, A_SHARP -> new KeyPoints(
                    new double[]{
                            center - (blackKeyWidth / 2.0) + padding,
                            center + (blackKeyWidth / 2.0) - padding,
                            center + (blackKeyWidth / 2.0) - padding,
                            center - (blackKeyWidth / 2.0) + padding,
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
     */

    private static double F_SHARP_OFFSET = 0.00;

    public static KeyPoints pointsForKey(Key.Note note, double topLeft, double y, double h, double whiteKeyWidth, double blackKeyWidth, double padding) {
        var bwRatio = whiteKeyWidth / blackKeyWidth;
        var bwDiff = whiteKeyWidth - blackKeyWidth;
        return switch(note) {
            case C -> new KeyPoints(
                    new double[]{
                            topLeft + padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + whiteKeyWidth - padding,
                            topLeft + whiteKeyWidth - padding,
                            topLeft + padding
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
            case D -> new KeyPoints(
                    new double[]{
                            topLeft + padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + blackKeyWidth + bwDiff / 2.0 - padding,
                            topLeft + blackKeyWidth + bwDiff / 2.0 - padding,
                            topLeft - bwDiff / 2.0 + padding,
                            topLeft - bwDiff / 2.0 + padding,
                            topLeft + padding
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
            case E -> new KeyPoints(
                    new double[]{
                            topLeft + padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft - bwDiff + padding,
                            topLeft - bwDiff + padding,
                            topLeft + padding
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
            case F -> new KeyPoints(
                    new double[]{
                            topLeft + padding,
                            topLeft - blackKeyWidth * F_SHARP_OFFSET + blackKeyWidth - padding,
                            topLeft - blackKeyWidth * F_SHARP_OFFSET + blackKeyWidth - padding,
                            topLeft - blackKeyWidth * F_SHARP_OFFSET + whiteKeyWidth - padding,
                            topLeft - blackKeyWidth * F_SHARP_OFFSET + whiteKeyWidth - padding,
                            topLeft + padding,
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
            case G -> new KeyPoints(
                    new double[]{
                            topLeft + padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + blackKeyWidth + (blackKeyWidth / 2.0) - padding,
                            topLeft + blackKeyWidth + (blackKeyWidth / 2.0) - padding,
                            topLeft + blackKeyWidth + (blackKeyWidth / 2.0) - whiteKeyWidth + padding,
                            topLeft + blackKeyWidth + (blackKeyWidth / 2.0) - whiteKeyWidth + padding,
                            topLeft + padding,
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
            case A -> new KeyPoints(
                    new double[]{
                            topLeft + padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + whiteKeyWidth - blackKeyWidth / 2.0 - padding,
                            topLeft + whiteKeyWidth - blackKeyWidth / 2.0 - padding,
                            topLeft - (blackKeyWidth / 2.0) + padding,
                            topLeft - (blackKeyWidth / 2.0) + padding,
                            topLeft + padding,
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
            case B -> new KeyPoints(
                    new double[]{
                            topLeft + padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + blackKeyWidth - whiteKeyWidth + padding,
                            topLeft + blackKeyWidth - whiteKeyWidth + padding,
                            topLeft + padding,
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
            case F_SHARP -> new KeyPoints(
                    new double[]{
                            topLeft - F_SHARP_OFFSET * blackKeyWidth + padding,
                            topLeft - F_SHARP_OFFSET * blackKeyWidth + blackKeyWidth - padding,
                            topLeft - F_SHARP_OFFSET * blackKeyWidth + blackKeyWidth - padding,
                            topLeft - F_SHARP_OFFSET * blackKeyWidth + padding,
                    },
                    new double[]{
                            y,
                            y,
                            y + h * BLACK_KEY_HEIGHT_FACTOR - padding,
                            y + h * BLACK_KEY_HEIGHT_FACTOR - padding,
                    },
                    4);
            case C_SHARP, D_SHARP, G_SHARP, A_SHARP -> new KeyPoints(
                    new double[]{
                            topLeft + padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + blackKeyWidth - padding,
                            topLeft + padding,
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
