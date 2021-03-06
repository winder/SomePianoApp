package com.willwinder.rtp.util;

import com.google.common.eventbus.EventBus;
import com.willwinder.rtp.model.Key;
import com.willwinder.rtp.model.KeyboardState;
import com.willwinder.rtp.util.NoteEvent;
import com.willwinder.rtp.view.NoiseMaker;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import java.util.*;

import static com.willwinder.rtp.util.Util.midiMessageToKey;

/**
 * A simple MIDI receiver which maintains a cache of the active keyboard state.
 */
public class KeyboardReceiver implements Receiver, KeyboardState {
    private final EventBus eventBus;
    private final NoiseMaker noise;

    public KeyboardReceiver(EventBus eventBus, NoiseMaker noise) {
        this.eventBus = eventBus;
        this.noise = noise;
    }

    private final Map<Integer, Key> activeKeys = new HashMap<>();

    @Override
    public void send(MidiMessage message, long timeStamp) {
        //System.out.println(midiMessageToString(message));

        midiMessageToKey(message).ifPresent(k -> {
            var event = new NoteEvent(k, 99, System.currentTimeMillis());
            noise.noteEvent(event);

            synchronized (activeKeys) {
                if (k.isActive()) {
                    activeKeys.put(k.key, k);
                } else {
                    activeKeys.remove(k.key);
                }
            }
            eventBus.post(event);
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
