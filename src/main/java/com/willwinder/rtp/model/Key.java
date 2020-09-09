package com.willwinder.rtp.model;

import com.willwinder.rtp.util.Util;

import javax.sound.midi.ShortMessage;

public record Key(Note note, int octave, int command, int status, int key, int velocity) {
    public static enum Note {
        C("C"),
        C_SHARP("C#"),
        D("D"),
        D_SHARP("D#"),
        E("E"),
        F("F"),
        F_SHARP("F#"),
        G("G"),
        G_SHARP("G#"),
        A("A"),
        A_SHARP("A#"),
        B("B");

        public final String name;

        Note(String name) {
            this.name = name;
        }
    }
    public boolean isActive() {
        return this.command == ShortMessage.NOTE_ON;
    }

    @Override
    public String toString() {
        String command = Util.commandToString(this.command);
        String status = Util.statusToString(this.status);

        return new StringBuilder()
                .append(note)
                .append(octave)
                .append(" (")
                .append(key)
                .append(")")
                .append(" --  command: ")
                .append(command)
                .append(", status: ")
                .append(status)
                .append(", velocity: ")
                .append(velocity)
                .toString();
    }
}
