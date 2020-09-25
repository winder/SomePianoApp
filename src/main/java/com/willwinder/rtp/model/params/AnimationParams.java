package com.willwinder.rtp.model.params;

import javafx.beans.property.*;

/**
 * These are separate from the rest because they aren't global.
 */
public class AnimationParams {
    public final BooleanProperty showKeyboard;
    public final BooleanProperty showTimeline;
    public final BooleanProperty showStaff;

    public AnimationParams(boolean showKeyboard, boolean showTimeline, boolean showStaff) {
        this.showKeyboard = new SimpleBooleanProperty(showKeyboard);
        this.showTimeline = new SimpleBooleanProperty(showTimeline);
        this.showStaff = new SimpleBooleanProperty(showStaff);
    }
}
