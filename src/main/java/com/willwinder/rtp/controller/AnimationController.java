package com.willwinder.rtp.controller;

import com.willwinder.rtp.graphics.Renderable;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static com.willwinder.rtp.Constants.DEFAULT_WIDTH;

/**
 * This is the main loop, and is sort of an controller in the MVC sense of the word. The JavaFX runtime is
 * responsible for calling handle when the canvas should be repainted.
 *
 * Drawing is done using a simple Actor model. Any number of Renderable objects can be added for drawing.
 */
public class AnimationController extends AnimationTimer {
    private final GraphicsContext gc;
    private final List<Renderable> renderableList = new ArrayList<>();
    private double w, h = 0.0;

    public AnimationController(GraphicsContext gc) {
        this.gc = gc;
    }

    public void addRenderable(Renderable r) {
        if (!renderableList.contains(r)) {
            renderableList.add(r);
        }
    }

    public void removeRenderable(Renderable r) {
        renderableList.remove(r);
    }

    @Override
    public void handle(long l) {
        double height = gc.getCanvas().getHeight();
        double width = gc.getCanvas().getWidth();

        boolean reset = false;
        if (w != width || h != height) {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, width, height);
            this.w = width;
            this.h = height;
            reset = true;
        }

        long now = System.currentTimeMillis();

        Renderable.DrawParams drawParams = new Renderable.DrawParams(w, h, reset, now);

        for (Renderable r : renderableList) {
            try {
                r.draw(gc, drawParams);
            } catch (Renderable.RenderableException e) {
                e.printStackTrace();
            }
        }
    }
}
