package com.willwinder.rtp.model.params;

import com.google.common.eventbus.EventBus;
import com.willwinder.rtp.graphics.KeyPointCache;
import com.willwinder.rtp.model.KeyboardState;
import com.willwinder.rtp.util.KeyboardReceiver;

public class AllParams {
    public final KeyPointCacheParams keyPointCacheParams;
    public final KeyPointCache keyPointCache;
    public final TimelineParams timelineParams;
    public final BPMParams bpmParams;
    public final EventBus eventBus;
    public final KeyboardState keyboardState;
    public final KeyboardReceiver keyboardReceiver;

    public AllParams(KeyPointCacheParams keyPointCacheParams,
                     KeyPointCache keyPointCache,
                     TimelineParams timelineParams,
                     BPMParams bpmParams,
                     EventBus eventBus,
                     KeyboardState keyboardState,
                     KeyboardReceiver keyboardReceiver) {
        this.keyPointCacheParams = keyPointCacheParams;
        this.keyPointCache = keyPointCache;
        this.timelineParams = timelineParams;
        this.bpmParams = bpmParams;
        this.eventBus = eventBus;
        this.keyboardState = keyboardState;
        this.keyboardReceiver = keyboardReceiver;
    }
}
