package com.willwinder.rtp.controller;

import com.willwinder.rtp.KeyboardState;
import com.willwinder.rtp.graphics.KeyGraphicsUtils;
import com.willwinder.rtp.model.Key;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class AnimationController extends AnimationTimer {
    private final GraphicsContext gc;
    private final KeyboardState state;

    public AnimationController(GraphicsContext gc, KeyboardState state) {
        this.gc = gc;
        this.state = state;
    }

    private void printWidth(String prefix, double[] points) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double point : points) {
            if (point < min) {
                min = point;
            }
            if (point > max) {
                max = point;
            }
        }

        System.out.println(prefix + " width: " + (max-min) + ", min: " + min + ", max: " + max);
    }

    @Override
    public void handle(long l) {
        var height = gc.getCanvas().getHeight();
        var width = gc.getCanvas().getWidth();
        //System.out.println("height: " + gc.getCanvas().getHeight());
        //System.out.println("width: " + gc.getCanvas().getWidth());

        var MULT = 3.0;
        var BLACK_KEY_WIDTH = 14 * MULT;
        var WHITE_KEY_WIDTH = 23 * MULT;
        var KEY_HEIGHT = 100 * MULT;
        var CENTER_OFFSET = 125;
        var PADDING = 1;
        var WHITE_KEY_WIDTH_DIV_2 = WHITE_KEY_WIDTH / 2.0;

        gc.clearRect(0, 0, width, height);
        //gc.setFill(Color.BLUE);
        //gc.fillRect(75,75,100,100);

        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.C, CENTER_OFFSET, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("C", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }
        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.C_SHARP, CENTER_OFFSET + BLACK_KEY_WIDTH, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("C#", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }
        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.D, CENTER_OFFSET + 2 * BLACK_KEY_WIDTH, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("D", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }
        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.D_SHARP, CENTER_OFFSET + 3 * BLACK_KEY_WIDTH, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("D#", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }
        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.E, CENTER_OFFSET + 4 * BLACK_KEY_WIDTH, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("E", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }
        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.F, CENTER_OFFSET + 5 * BLACK_KEY_WIDTH, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("F", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }
        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.F_SHARP, CENTER_OFFSET + 6 * BLACK_KEY_WIDTH, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("F#", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }
        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.G, CENTER_OFFSET + 7 * BLACK_KEY_WIDTH, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("G", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }
        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.G_SHARP, CENTER_OFFSET + 8 * BLACK_KEY_WIDTH, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("G#", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }
        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.A, CENTER_OFFSET + 9 * BLACK_KEY_WIDTH, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("A", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }
        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.A_SHARP, CENTER_OFFSET + 10 * BLACK_KEY_WIDTH, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("A#", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }
        {
            Color c = new Color(Math.random(), Math.random(), Math.random(), Math.random());
            gc.setFill(c);
            var kp = KeyGraphicsUtils.pointsForKey(Key.Note.B, CENTER_OFFSET + 11 * BLACK_KEY_WIDTH, 200, KEY_HEIGHT, WHITE_KEY_WIDTH, BLACK_KEY_WIDTH, PADDING);
            printWidth("B", kp.xPoints());
            gc.fillPolygon(kp.xPoints(), kp.yPoints(), kp.numPoints());
        }

        gc.setFill(Color.WHITE);
        gc.fillText(String.valueOf(state.getActiveKeys().size()), 10.0, 10.0);
    }
}
