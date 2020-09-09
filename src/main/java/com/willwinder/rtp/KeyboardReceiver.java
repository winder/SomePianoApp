package com.willwinder.rtp;

import com.willwinder.rtp.model.Key;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.willwinder.rtp.util.Util.midiMessageToKey;
import static com.willwinder.rtp.util.Util.midiMessageToString;

public class KeyboardReceiver implements Receiver, KeyboardState {
    private Map<Integer, Key> activeKeys = new HashMap<>();

    @Override
    public void send(MidiMessage message, long timeStamp) {
        System.out.println(midiMessageToString(message));

        midiMessageToKey(message).ifPresent(k -> {
            synchronized (activeKeys) {
                if (k.isActive()) {
                    System.out.println("Adding: " + k.toString());
                    activeKeys.put(k.key(), k);
                } else {
                    System.out.println("Removing: " + k.toString());
                    activeKeys.remove(k.key());
                }
            }
        });
    }

    @Override
    public void close() {

    }

    @Override
    public Collection<Key> getActiveKeys() {
        return activeKeys.values();
    }
}
