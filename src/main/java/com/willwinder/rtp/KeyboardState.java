package com.willwinder.rtp;

import com.willwinder.rtp.model.Key;

import java.util.Map;
import java.util.Set;

/**
 * Active key cache interface.
 */
public interface KeyboardState {
    /**
     * Get a collection of active keys being pressed on a MIDI transmitter.
     * @return collection of Key objects.
     */
    Map<Integer, Key> getActiveKeys();

    /**
     * Get a collection of active keys being pressed on a MIDI transmitter.
     * @return collection of Key codes.
     */
    Set<Integer> getActiveKeyCodes();
}
