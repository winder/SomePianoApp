package com.willwinder.rtp.view;

import com.willwinder.rtp.util.NoteEvent;

import javax.sound.midi.*;

import org.jfugue.midi.MidiDictionary;
import org.jfugue.player.Player;
import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Note;


public class NoiseMaker {
    RealtimePlayer player;

    private javax.sound.midi.Synthesizer synth;
    private Instrument instruments[];
    private MidiChannel channel;

    public NoiseMaker() {
        try {
            player = new RealtimePlayer();
            player.changeInstrument(101);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        /*
        try {
            this.synth = MidiSystem.getSynthesizer();
            synth.open();

            Soundbank sb = synth.getDefaultSoundbank();
            if (sb != null) {
                instruments = sb.getInstruments();
                synth.loadInstrument(instruments[0]);
                channel = synth.getChannels()[0];
            }
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
         */
    }

    //public void noteEvent(NoteEvent e) {
    //@Subscribe
    synchronized public void noteEvent(NoteEvent event) {
        //System.out.println("Synth latency: " + this.synth.getLatency());
        //if (channel == null) return;
        Note n = new Note(event.key.key);
        if (event.key.isActive()) {
            //n.setOnVelocity(event.key.velocity);
            player.startNote(n);
            //channel.noteOn(event.key.key, event.key.velocity);
        } else {
            player.stopNote(n);
            //channel.noteOff(event.key.key, event.key.velocity);
        }
    }
}
