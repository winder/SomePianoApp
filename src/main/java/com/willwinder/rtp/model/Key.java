package com.willwinder.rtp.model;

import com.willwinder.rtp.util.Util;

import javax.sound.midi.ShortMessage;

public class Key {
    public final Note note;
    public final int octave;
    public final int command;
    public final int status;
    public final int key;
    public final int velocity;
    public final int hand;

    public Key(Note note, int octave, int command, int status, int key, int velocity, int hand) {
        this.note = note;
        this.octave = octave;
        this.command = command;
        this.status = status;
        this.key = key;
        this.velocity = velocity;
        this.hand = hand;
    }

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
        private final static Note[] values = values();

        Note(String name) {
            this.name = name;
        }

        /**
         * Given a key number returns the corresponding note.
         * @param key index of key starting with A0 = 21.
         * @return the corresponding Note enum.
         */
        public static Note noteForKey(int key) {
            return values[key % 12];
        }

        /**
         * Gets the note to the right of a given note.
         * @return the corresponding Note enum.
         */
        public Note nextNote() {
            return values[(this.ordinal() + 1) % values.length];
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
            if(this.ordinal() + 1 < values.length) {
                return values[this.ordinal() + 1].name.contains("#");
            }

            // When wrapping octaves from B -> C, it is a full tone, not a semitone.
            return false;
        }
    }

    public boolean isActive() {
        return this.command == ShortMessage.NOTE_ON && this.velocity != 0;
    }

    @Override
    public String toString() {
        String command = Util.commandToString(this.command);
        String status = Util.statusToString(this.status);

        return String.valueOf(note) +
                octave +
                " (" +
                key +
                ")" +
                " --  command: " +
                command +
                ", status: " +
                status +
                ", velocity: " +
                velocity;
    }
}
