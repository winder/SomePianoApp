package com.willwinder.rtp.view;

import com.willwinder.rtp.util.NoteEvent;

import javax.sound.midi.*;

public class NoiseMaker {
    private Synthesizer synth;
    private Instrument instruments[];
    private MidiChannel channel;

    public NoiseMaker() {
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
    }

    //public void noteEvent(NoteEvent e) {
    //@Subscribe
    synchronized public void noteEvent(NoteEvent event) {
        if (channel == null) return;
        if (event.key.isActive()) {
            channel.noteOn(event.key.key, event.key.velocity);
        } else {
            channel.noteOff(event.key.key, event.key.velocity);
        }
    }
}
