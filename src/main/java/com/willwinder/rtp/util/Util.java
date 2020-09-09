package com.willwinder.rtp.util;

import com.willwinder.rtp.model.Key;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import java.util.Optional;

import static com.willwinder.rtp.model.Key.Note.*;

public class Util {
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    public static final Key.Note[] NOTE_ENUMS = {C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G, G_SHARP, A, A_SHARP, B};

    public static int dataToOctave(int key) {
        return (key / 12) - 1;
    }

    public static Key.Note dataToNote(int key) {
        int note = key % 12;
        return NOTE_ENUMS[note];
    }

    public static String commandToString(int command) {
        return switch(command) {
            case ShortMessage.CHANNEL_PRESSURE -> "CHANNEL_PRESSURE";
            case ShortMessage.CONTROL_CHANGE   -> "CONTROL_CHANGE";
            case ShortMessage.NOTE_OFF         -> "NOTE_OFF";
            case ShortMessage.NOTE_ON          -> "NOTE_ON";
            case ShortMessage.PITCH_BEND       -> "PITCH_BEND";
            case ShortMessage.POLY_PRESSURE    -> "POLY_PRESSURE";
            case ShortMessage.PROGRAM_CHANGE   -> "PROGRAM_CHANGE";
            default                            -> "UNKNOWN (" + command + ")";
        };
    }

    public static String statusToString(int status) {
        return switch (status) {
            case ShortMessage.ACTIVE_SENSING        -> "ACTIVE_SENSING";
            case ShortMessage.CONTINUE              -> "CONTINUE";
            case ShortMessage.END_OF_EXCLUSIVE      -> "END_OF_EXCLUSIVE";
            case ShortMessage.MIDI_TIME_CODE        -> "MIDI_TIME_CODE";
            case ShortMessage.SONG_POSITION_POINTER -> "SONG_POSITION_POINTER";
            case ShortMessage.SONG_SELECT           -> "SONG_SELECT";
            case ShortMessage.START                 -> "START";
            case ShortMessage.STOP                  -> "STOP";
            case ShortMessage.SYSTEM_RESET          -> "SYSTEM_RESET";
            case ShortMessage.TIMING_CLOCK          -> "TIMING_CLOCK";
            case ShortMessage.TUNE_REQUEST          -> "TUNE_REQUEST";
            default                                 -> "UNKNOWN (" + status + ")";
        };
    }

    public static Optional<Key> midiMessageToKey(MidiMessage message) {
        if (message instanceof ShortMessage sm) {
            Key k = new Key(dataToNote(sm.getData1()), dataToOctave(sm.getData1()), sm.getCommand(), sm.getStatus(), sm.getData1(), sm.getData2());
            return Optional.of(k);
        }

        return Optional.empty();
    }

    public static String midiMessageToString(MidiMessage message) {
        return midiMessageToKey(message)
                .map(Key::toString)
                .orElseGet(() ->
                    new StringBuilder()
                            .append("Message (")
                            .append(message.toString())
                            .append("), status: ")
                            .append(message.getStatus())
                            .append(", message: ")
                            .append(message.getMessage())
                            .append(", length: ")
                            .append(message.getLength())
                            .toString());
    }
}
