package com.willwinder.rtp.model;

import com.willwinder.rtp.util.Util;

import javax.sound.midi.ShortMessage;

public record Key(Note note, int octave, int command, int status, int key, int velocity) {
    public enum Note {
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
        private static Note[] vals = values();

        Note(String name) {
            this.name = name;
        }

        /**
         * Get the key index, starting with C = 0
         */
        public int keyIndex() {
            return this.ordinal();
        }

        /**
         * Whether or not this note is played by a black key.
         * @return true if the note is a black key, otherwise false.
         */
        public boolean isBlackKey() {
            return this.name.contains("#");
        }

        /**
         * Whether or not this note is played by a white key.
         * @return true if the note is a white key, otherwise false.
         */
        public boolean isWhiteKey() {
            return !isBlackKey();
        }

        /**
         * Compute whether or not the interval between a note and the next note is a semitone
         * rather than a whole tone.
         * @param scale the scale being used. Currently only C scale is supported.
         * @return true if the next note is a semitone, otherwise false.
         */
        public boolean nextNoteIntervalIsSemitone(Note scale) {
            if (scale != Note.C) {
                throw new IllegalArgumentException("Only C scale is supported.");
            }

            // Next note after a sharp is a semitone up.
            if (this.name.contains("#")) {
                return true;
            }
            // If not a sharp, only a semitone if the next note is.
            if(this.ordinal() + 1 < vals.length) {
                return vals[this.ordinal() + 1].name.contains("#");
            }

            // When wrapping octaves from B -> C, it is a full tone, not a semitone.
            return false;
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
