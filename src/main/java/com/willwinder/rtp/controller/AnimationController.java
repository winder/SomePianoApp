package com.willwinder.rtp.controller;

import com.willwinder.rtp.KeyboardState;
import com.willwinder.rtp.graphics.Renderable;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

import static com.willwinder.rtp.Constants.DEFAULT_WIDTH;

/**
 * This is the main loop, and is sort of an controller in the MVC sense of the word. The JavaFX runtime is
 * responsible for calling handle when the canvas should be repainted.
 *
 * Drawing is done using a simple Actor model. Any number of {@Renderable} objects can be added for drawing.
 */
public class AnimationController extends AnimationTimer {
    private final GraphicsContext gc;
    private final KeyboardState state;
    private final List<Renderable> renderableList = new ArrayList<>();

    public AnimationController(GraphicsContext gc, KeyboardState state) {
        this.gc = gc;
        this.state = state;
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
        var height = gc.getCanvas().getHeight();
        var width = gc.getCanvas().getWidth();
        var scale = width / Double.valueOf(DEFAULT_WIDTH);
        gc.clearRect(0, 0, width, height);

        for (Renderable r : renderableList) {
            try {
                r.draw(gc, scale);
            } catch (Renderable.RenderableException e) {
                e.printStackTrace();
            }
        }
    }
}
