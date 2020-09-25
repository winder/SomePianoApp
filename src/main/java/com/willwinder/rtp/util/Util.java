package com.willwinder.rtp.util;

import com.willwinder.rtp.model.Key;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import java.util.Arrays;
import java.util.Optional;

import static com.willwinder.rtp.model.Key.Note.*;

public class Util {
    public static final Key.Note[] NOTE_ENUMS = {C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G, G_SHARP, A, A_SHARP, B};

    public static int dataToOctave(int key) {
        return ((key+3) / 12) - 1;
    }

    public static Key.Note dataToNote(int key) {
        int note = key % 12;
        return NOTE_ENUMS[note];
    }

    public static String commandToString(int command) {
        switch(command) {
            case ShortMessage.CHANNEL_PRESSURE: return "CHANNEL_PRESSURE";
            case ShortMessage.CONTROL_CHANGE  : return "CONTROL_CHANGE";
            case ShortMessage.NOTE_OFF        : return "NOTE_OFF";
            case ShortMessage.NOTE_ON         : return "NOTE_ON";
            case ShortMessage.PITCH_BEND      : return "PITCH_BEND";
            case ShortMessage.POLY_PRESSURE   : return "POLY_PRESSURE";
            case ShortMessage.PROGRAM_CHANGE  : return "PROGRAM_CHANGE";
            default                           : return "UNKNOWN (" + command + ")";
        }
    }

    public static String statusToString(int status) {
        switch (status) {
            case ShortMessage.ACTIVE_SENSING       : return "ACTIVE_SENSING";
            case ShortMessage.CONTINUE             : return "CONTINUE";
            case ShortMessage.END_OF_EXCLUSIVE     : return "END_OF_EXCLUSIVE";
            case ShortMessage.MIDI_TIME_CODE       : return "MIDI_TIME_CODE";
            case ShortMessage.SONG_POSITION_POINTER: return "SONG_POSITION_POINTER";
            case ShortMessage.SONG_SELECT          : return "SONG_SELECT";
            case ShortMessage.START                : return "START";
            case ShortMessage.STOP                 : return "STOP";
            case ShortMessage.SYSTEM_RESET         : return "SYSTEM_RESET";
            case ShortMessage.TIMING_CLOCK         : return "TIMING_CLOCK";
            case ShortMessage.TUNE_REQUEST         : return "TUNE_REQUEST";
            default                                : return "UNKNOWN (" + status + ")";
        }
    }

    public static Optional<Key> midiMessageToKey(MidiMessage message) {
        if (message instanceof ShortMessage) {
            ShortMessage sm = (ShortMessage)message;
            if (sm.getCommand() == ShortMessage.NOTE_ON || sm.getCommand() == ShortMessage.NOTE_OFF) {
                Key k = new Key(dataToNote(sm.getData1()), dataToOctave(sm.getData1()), sm.getCommand(), sm.getStatus(), sm.getData1(), sm.getData2(), 0);
                return Optional.of(k);
            }
        }

        return Optional.empty();
    }

    public static String midiMessageToString(MidiMessage message) {
        return midiMessageToKey(message)
                .map(Key::toString)
                .orElseGet(() -> {
                    if (message instanceof MetaMessage) {
                        MetaMessage mm = (MetaMessage) message;
                        return "Message (" +
                                mm.toString() +
                                "): " + MetaUtils.metaTypeToString(mm);
                    } else {
                        return "Message (" +
                                message.toString() +
                                "), status: " +
                                message.getStatus() +
                                ", message: " +
                                Arrays.toString(message.getMessage()) +
                                ", length: " +
                                message.getLength();
                    }
                });
    }
}
