package com.willwinder.rtp.model.params;

import com.google.common.eventbus.EventBus;
import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.model.KeyboardState;
import com.willwinder.rtp.util.KeyboardReceiver;

// TODO: Need to save the parameters somehow.
public class AllParams {
    public final KeyPointCacheParams keyPointCacheParams;
    public final KeyPointCache keyPointCache;
    public final TimelineParams timelineParams;
    public final BPMParams bpmParams;
    public final GrandStaffParams grandStaffParams;
    public final EventBus eventBus;
    public final KeyboardState keyboardState;
    public final KeyboardReceiver keyboardReceiver;
    public final ControllerParams controllerParams;
    public final AnimationParams animationParams;

    public AllParams(KeyPointCacheParams keyPointCacheParams,
                     KeyPointCache keyPointCache,
                     TimelineParams timelineParams,
                     BPMParams bpmParams,
                     GrandStaffParams grandStaffParams,
                     EventBus eventBus,
                     KeyboardState keyboardState,
                     KeyboardReceiver keyboardReceiver,
                     ControllerParams controllerParams,
                     AnimationParams animationParams) {
        this.keyPointCacheParams = keyPointCacheParams;
        this.keyPointCache = keyPointCache;
        this.timelineParams = timelineParams;
        this.bpmParams = bpmParams;
        this.grandStaffParams = grandStaffParams;
        this.eventBus = eventBus;
        this.keyboardState = keyboardState;
        this.keyboardReceiver = keyboardReceiver;
        this.controllerParams = controllerParams;
        this.animationParams = animationParams;
    }
}
