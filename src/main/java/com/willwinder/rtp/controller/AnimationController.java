package com.willwinder.rtp.controller;

import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.graphics.Renderable;
import com.willwinder.rtp.graphics.RenderableFps;
import com.willwinder.rtp.graphics.RenderableGroup;
import com.willwinder.rtp.graphics.renderables.BPMLines;
import com.willwinder.rtp.graphics.renderables.KeyboardView;
import com.willwinder.rtp.graphics.renderables.TimelineBackground;
import com.willwinder.rtp.graphics.renderables.TimelineSparks;
import com.willwinder.rtp.model.params.AllParams;
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
    private KeyPointCache keyPointCache;
    private final List<Renderable> renderableList = new ArrayList<>();

    // For detecting a reset is required.
    private double w, h = 0.0;
    private int currentKeyPointHash = 0;

    public AnimationController(GraphicsContext gc, AllParams params) {
        this.gc = gc;
        this.keyPointCache = params.keyPointCache;

        KeyboardView keyboardView = new KeyboardView(params.keyboardState, params.keyPointCache);

        TimelineBackground timelineBackground = new TimelineBackground(params.timelineParams);
        TimelineSparks timelineSparks = new TimelineSparks(params.timelineParams, params.keyboardState);

        BPMLines bpm = new BPMLines(params.bpmParams);

        RenderableGroup timeline = new RenderableGroup(
                timelineBackground,
                bpm,
                timelineSparks
        );
        RenderableFps timelineFps = new RenderableFps(timeline, 40);

        //////////////////////////
        // Register renderables //
        //////////////////////////
        addRenderable(keyboardView);
        addRenderable(timelineBackground);
        addRenderable(bpm);
        addRenderable(timelineSparks);

        ////////////////////////
        // Limit Timeline FPS //
        ////////////////////////
        //ac.addRenderable(keyboardView);
        //ac.addRenderable(timelineFps);

        ///////////////
        // Debugging //
        ///////////////
        //ac.addRenderable(new NumKeysView(receiver));

        // Register listeners
        params.eventBus.register(keyboardView);
        params.eventBus.register(timelineSparks);
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
        int kpHash = this.keyPointCache.params.hashCode();
        if (w != width || h != height || currentKeyPointHash != kpHash) {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, width, height);
            this.w = width;
            this.h = height;
            this.currentKeyPointHash = kpHash;
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
