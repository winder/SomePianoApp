package com.willwinder.rtp.util;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Optional;

public class MetaUtils {
    public static final Charset charset = Charset.forName("ISO_8859-1");
    public static final CharsetEncoder encoder = charset.newEncoder();
    public static final CharsetDecoder decoder = charset.newDecoder();

    public static String bytesToText(byte[] b) {
        String result = null;
        decoder.reset();
        try {
            result = decoder.decode(ByteBuffer.wrap(b)).toString();
        } catch (CharacterCodingException e) {
            e.printStackTrace();
            result = "Problem decoding " + e.getMessage();
        }
        return result;
    }

    enum MetaType {
        /**
         * General "Text", used for any text based data.
         */
        META_TEXT(0x01),
        /**
         * Meta text copyright.
         */
        META_COPYRIGHT(0x02),
        /**
         * Track name.
         */
        META_TRACK_NAME(0x03),
        /**
         * Instrument name.
         */
        META_INSTRUMENT_NAME(0x04),
        /**
         * Lyrics of a song. Typically one syllable per MetaMessage.
         */
        META_LYRICS(0x05),
        /**
         * Marks a point of interest in a MIDI file, for example beginning of
         * a verse, solo, etc.
         */
        META_MARKER(0x06),
        /**
         * Meta text cue point. For example 'Cue performer 1'.
         */
        META_CUE_POINT(0x07),
        /**
         * @see <a href="http://www.midi.org/techspecs/rp19.php">SMF Device Name and
         *      Program Name Meta Events</a>
         */
        META_PROGRAM_NAME(0x08),
        // FF 08 len text PROGRAM NAME
        /**
         * Meta text device name.
         */
        META_DEVICE_NAME(0x09),
        /**
         * Empty meta message that marks the end of a track.
         */
        META_END_OF_TRACK(0x2f),
        // FF 09 len text DEVICE NAME
        /**
         * Tempo is in microseconds per beat (quarter note).
         */
        META_TEMPO(0x51),
        /**
         * SMPTE offset:
         * - frame_rate: {24|25|29.97|30}, default 24
         * - hours: {0..255}
         * - minutes: {0..59}
         * - seconds: {0..59}
         * - frames: {0..255}
         * - sub_frames: {0..99}
         */
        META_SMPTE_OFFSET(0x54),
        /**
         * Time signature message:
         * - numerator: {0..255}, default 4
         * - denominator: {1..2**255}, default 4
         * - clocks_per_click: {0..255}, default 24
         * - notated_32nd_notes_per_beat: {0..255}, default 8
         *
         * 4/4 : MetaMessage(‘time_signature’, numerator=4, denominator=4)
         * 3/8 : MetaMessage(‘time_signature’, numerator=3, denominator=8)
         */
        META_TIME_SIG(0x58),
        /**
         * Meta text key signature.
         */
        META_KEY_SIG(0x59),

        UNKNOWN(-1),

        // Below are obsolete
        /**
         * Prefix for the channel where events are played.
         */
        META_CHANNEL_PREFIX(0x20),
        /**
         * MIDI port where events are played.
         */
        META_MIDI_PORT(0x21);

        public static Optional<MetaType> convertToType(int type) {
            for (MetaType t : values()) {
                if (t.type == type) return Optional.of(t);
            }
            return Optional.of(UNKNOWN);
        }

        public final int type;
        MetaType(int type) {
            this.type = type;
        }
    }

    public static class TimeSignatureData {
        public final int numerator;
        public final int denominator;
        public final int clocksPerTick;
        public final int notated32ndNotesPerBeat;

        public TimeSignatureData(byte[] data) {
            this.numerator = data[0];
            this.denominator = data[1] * data[1];
            this.clocksPerTick = data[2];
            this.notated32ndNotesPerBeat = data[3];
        }

        @Override
        public String toString() {
            return "numerator: " + numerator + ", " +
                    "denominator: " + denominator + ", " +
                    "clocksPerTick: " + clocksPerTick + ", " +
                    "notated 32nd notes per beat: " + notated32ndNotesPerBeat;
        }
    }

    public static class KeySignatureData {
        final int num;
        final boolean sharps;
        final boolean major;
        public KeySignatureData(byte[] data) {
            if (data[0] < 0) {
                this.sharps = false;
            } else if (data[0] > 0) {
                this.sharps = true;
            } else {
                this.sharps = false;
            }
            if (1 == data[1]) {
                this.major = false;
            } else {
                this.major = true;
            }
            this.num = Math.abs(data[0]);
        }
        @Override
        public String toString() {
            String[] sharpKeys = { "C", "G", "D", "A", "E", "B", "F#", "C#" };
            String[] flatKeys = { "C", "F", "Bb", "Eb", "Ab", "Db", "Gb", "Cb" };
            String[] sharpMinorKeys = { "A", "E", "B", "F#", "C#", "G#", "D#", "A#" };
            String[] flatMinorKeys = { "A", "D", "G", "C", "F", "Bb", "Eb", "Ab" };
            String str = " " + num;
            if (sharps) {
                str += " sharps ";
            } else {
                str += " flats ";
            }

            if (major) {
                str += " major ";
                if (sharps) {
                    str += sharpKeys[num];
                } else {
                    str += flatKeys[num];
                }

            } else {
                str += " minor ";
                if (sharps) {
                    str += sharpMinorKeys[num];
                } else {
                    str += flatMinorKeys[num];
                }
            }
            return str;
        }
    }
    public static int parseTempMetaMessage(MetaMessage message) {
        var data = message.getData();
        int mask = 0xFF;
        int bvalue = (data[0] & mask);
        bvalue = (bvalue << 8) + (data[1] & mask);
        bvalue = (bvalue << 8) + (data[2] & mask);
        return 60000000 / bvalue;
    }

    public static String metaTypeToString(MetaMessage message) {
        MetaType type = MetaType.convertToType(message.getType())
                .orElse(null);

        if (type == null) {
            return message.getType() + ": Unknown meta type!";
        }

        switch(type) {
            case META_TEMPO:
                return type.toString() + ": " + parseTempMetaMessage(message);
            case META_TIME_SIG:
                return type.toString() + ": " + new TimeSignatureData(message.getData());
            case META_KEY_SIG:
                return type.toString() + ": " + new KeySignatureData(message.getData());
            case META_TRACK_NAME:
            case META_INSTRUMENT_NAME:
            case META_LYRICS:
            case META_CHANNEL_PREFIX:
            case META_MIDI_PORT:
            case META_END_OF_TRACK:
            case META_CUE_POINT:
            case META_DEVICE_NAME:
            case META_MARKER:
            case META_PROGRAM_NAME:
            case META_SMPTE_OFFSET:
            case META_TEXT:
            case META_COPYRIGHT:
                return type.toString() + ": " + bytesToText(message.getData());
            case UNKNOWN:
            default:
                return type.toString() + ": " + message.getType() + " - Unknown meta type! (" + bytesToText(message.getData()) + ")";
        }
    }
}
