package com.willwinder.rtp.view;

import com.willwinder.rtp.util.NoteEvent;

import javax.sound.midi.*;

import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Note;


public class NoiseMaker {
    public static enum Mode {
        JFUGUE,
        JAVAX
    }

    private final Mode mode;

    // Javax.sound mode
    private javax.sound.midi.Synthesizer synth;
    private Instrument instruments[];
    private MidiChannel channel;

    // JFugue mode
    private RealtimePlayer player;

    public NoiseMaker() {
        this(Mode.JAVAX);
    }

    public NoiseMaker(Mode mode) {
        this.mode = mode;
        try {
            switch(mode) {
                case JFUGUE:
                    player = new RealtimePlayer();
                    player.changeInstrument(0);
                case JAVAX:
                    this.synth = MidiSystem.getSynthesizer();
                    synth.open();

                    Soundbank sb = synth.getDefaultSoundbank();
                    if (sb != null) {
                        instruments = sb.getInstruments();
                        synth.loadInstrument(instruments[0]);
                        channel = synth.getChannels()[0];
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //public void noteEvent(NoteEvent e) {
    //@Subscribe
    synchronized public void noteEvent(NoteEvent event) {
        switch(mode) {
            case JFUGUE:
                Note n = new Note(event.key.key);
                if (event.key.isActive()) {
                    //n.setOnVelocity(event.key.velocity);
                    player.startNote(n);
                } else {
                    player.stopNote(n);
                }
            case JAVAX:
                //System.out.println("Synth latency: " + this.synth.getLatency());
                if (channel == null) return;
                if (event.key.isActive()) {
                    channel.noteOn(event.key.key, event.key.velocity);
                } else {
                    channel.noteOff(event.key.key, event.key.velocity);
                }
        }
    }
}
