package com.willwinder.rtp;

import com.willwinder.rtp.model.Key;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import java.util.*;

import static com.willwinder.rtp.util.Util.midiMessageToKey;
import static com.willwinder.rtp.util.Util.midiMessageToString;

/**
 * A simple MIDI receiver which maintains a cache of the active keyboard state.
 */
public class KeyboardReceiver implements Receiver, KeyboardState {
    private Map<Integer, Key> activeKeys = new HashMap<>();

    @Override
    public void send(MidiMessage message, long timeStamp) {
        //System.out.println(midiMessageToString(message));

        midiMessageToKey(message).ifPresent(k -> {
            synchronized (activeKeys) {
                if (k.isActive()) {
                    activeKeys.put(k.key(), k);
                } else {
                    activeKeys.remove(k.key());
                }
            }
        });
    }

    @Override
    public void close() {

    }

    @Override
    public HashMap<Integer, Key> getActiveKeys() {
        synchronized (activeKeys) {
            return new HashMap<>(activeKeys);
        }
    }

    @Override
    public Set<Integer> getActiveKeyCodes() {
        synchronized (activeKeys) {
            return new HashSet<>(activeKeys.keySet());
        }
    }
}
