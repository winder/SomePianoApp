package com.willwinder.rtp.view;

import com.willwinder.rtp.graphics.Renderable;
import com.willwinder.rtp.graphics.RenderableFps;
import com.willwinder.rtp.graphics.RenderableGroup;
import com.willwinder.rtp.graphics.renderables.*;
import com.willwinder.rtp.model.params.AllParams;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Drawing is done using a simple Actor model. Any number of Renderable objects can be added for drawing.
 * Getting the current time is outsourced to an external 'updateTime' event, this allows the controller class
 * to managed play/pause for renderables that need to be synchronized to the timeline.
 */
public class AnimateRenderables extends AnimationTimer {
    // Inject the time update from elsewhere, time gets funny during playback.
    public EventHandler<ActionEvent> updateTimelineTime;
    private GraphicsContext gc;
    private AllParams allParams;

    ///////////////////////////
    // Renderable management //
    ///////////////////////////
    private final List<Renderable> renderableList = new ArrayList<>();

    // For detecting a reset is required.
    private double w, h = 0.0;
    private int currentKeyPointHash = 0;

    public void addRenderable(Renderable r) {
        if (!renderableList.contains(r)) {
            renderableList.add(r);
        }
    }

    public void removeRenderable(Renderable r) {
        renderableList.remove(r);
    }

    public AnimateRenderables(EventHandler<ActionEvent> updateTimelineTime, GraphicsContext gc, AllParams params) {
        this.updateTimelineTime = updateTimelineTime;
        this.gc = gc;
        allParams = params;

        KeyboardView keyboardView = new KeyboardView(params.keyboardState, params.keyPointCache);

        TimelineBackground timelineBackground = new TimelineBackground(params.timelineParams);
        TimelineSparks timelineSparks = new TimelineSparks(params.timelineParams, params.keyboardState);

        GrandStaff grandStaff = new GrandStaff(params.timelineParams, params.grandStaffParams);

        BPMLines bpm = new BPMLines(params.bpmParams);

        RenderableGroup timeline = new RenderableGroup(
                timelineBackground,
                bpm,
                timelineSparks,
                grandStaff
        );
        RenderableFps timelineFps = new RenderableFps(timeline, 40);

        //////////////////////////
        // Register renderables //
        //////////////////////////
        addRenderable(keyboardView);
        addRenderable(timelineBackground);
        addRenderable(bpm);
        addRenderable(timelineSparks);
        addRenderable(grandStaff);

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
    }

    @Override
    public void handle(long now) {
        updateTimelineTime.handle(null);

        double height = gc.getCanvas().getHeight();
        double width = gc.getCanvas().getWidth();

        boolean reset = false;
        int kpHash = this.allParams.keyPointCache.params.hashCode();
        if (w != width || h != height || currentKeyPointHash != kpHash) {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, width, height);
            this.w = width;
            this.h = height;
            this.currentKeyPointHash = kpHash;
            reset = true;
        }

        Renderable.DrawParams drawParams = new Renderable.DrawParams(w, h, reset, this.allParams.timelineParams.nowMs.get());

        for (Renderable r : renderableList) {
            try {
                r.draw(gc, drawParams);
            } catch (Renderable.RenderableException e) {
                e.printStackTrace();
            }
        }
    }
}
