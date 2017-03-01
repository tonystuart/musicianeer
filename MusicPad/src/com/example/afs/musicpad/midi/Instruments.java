// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.midi;


public class Instruments {

  private final static String[] DRUMS = new String[] {
      "Acoustic Bass Drum", //
      "Bass Drum 1", //
      "Side Stick", //
      "Acoustic Snare",
      "Hand Clap", //
      "Electric Snare", //
      "Low Floor Tom", //
      "Closed Hi Hat",
      "High Floor Tom", //
      "Pedal Hi-Hat", //
      "Low Tom", //
      "Open Hi-Hat",
      "Low-Mid Tom", //
      "Hi-Mid Tom", //
      "Crash Cymbal 1", //
      "High Tom", //
      "Ride Cymbal 1", //
      "Chinese Cymbal", //
      "Ride Bell", //
      "Tambourine", //
      "Splash Cymbal", //
      "Cowbell", //
      "Crash Cymbal 2", //
      "Vibraslap", //
      "Ride Cymbal 2", //
      "Hi Bongo", //
      "Low Bongo", //
      "Mute Hi Conga", //
      "Open Hi Conga", //
      "Low Conga", //
      "High Timbale", //
      "Low Timbale", //
      "High Agogo", //
      "Low Agogo", //
      "Cabasa", //
      "Maracas", //
      "Short Whistle", //
      "Long Whistle", //
      "Short Guiro", //
      "Long Guiro", //
      "Claves", //
      "Hi Wood Block", //
      "Low Wood Block", //
      "Mute Cuica", //
      "Open Cuica", //
      "Mute Triangle", //
      "Open Triangle" //
  };

  private static String[] INSTRUMENTS = new String[] { //
      "acoustic-grand-piano", //
      "bright-acoustic-piano", //
      "electric-grand-piano", //
      "honky-tonk-piano", //
      "electric-piano-1", //
      "electric-piano-2", //
      "harpsichord", //
      "clavinet", //
      "celesta", //
      "glockenspiel", //
      "music-box", //
      "vibraphone", //
      "marimba", //
      "xylophone", //
      "tubular-bells", //
      "dulcimer", //
      "drawbar-organ", //
      "percussive-organ", //
      "rock-organ", //
      "church-organ", //
      "reed-organ", //
      "accordion", //
      "harmonica", //
      "tango-accordion", //
      "acoustic-guitar-nylon", //
      "acoustic-guitar-steel", //
      "electric-guitar-jazz", //
      "electric-guitar-clean", //
      "electric-guitar-muted", //
      "overdriven-guitar", //
      "distortion-guitar", //
      "guitar-harmonics", //
      "acoustic-bass", //
      "electric-bass-finger", //
      "electric-bass-pick", //
      "fretless-bass", //
      "slap-bass-1", //
      "slap-bass-2", //
      "synth-bass-1", //
      "synth-bass-2", //
      "violin", //
      "viola", //
      "cello", //
      "contrabass", //
      "tremolo-strings", //
      "pizzicato-strings", //
      "orchestral-harp", //
      "timpani", //
      "string-ensemble-1", //
      "string-ensemble-2", //
      "synth-strings-1", //
      "synth-strings-2", //
      "choir-aahs", //
      "voice-oohs", //
      "synth-choir", //
      "orchestra-hit", //
      "trumpet", //
      "trombone", //
      "tuba", //
      "muted-trumpet", //
      "french-horn", //
      "brass-section", //
      "synth-brass-1", //
      "synth-brass-2", //
      "soprano-sax", //
      "alto-sax", //
      "tenor-sax", //
      "baritone-sax", //
      "oboe", //
      "english-horn", //
      "bassoon", //
      "clarinet", //
      "piccolo", //
      "flute", //
      "recorder", //
      "pan-flute", //
      "blown-bottle", //
      "shakuhachi", //
      "whistle", //
      "ocarina", //
      "lead-1-square", //
      "lead-2-sawtooth", //
      "lead-3-calliope", //
      "lead-4-chiff", //
      "lead-5-charang", //
      "lead-6-voice", //
      "lead-7-fifths", //
      "lead-8-bass-+-lead", //
      "pad-1-new-age", //
      "pad-2-warm", //
      "pad-3-polysynth", //
      "pad-4-choir", //
      "pad-5-bowed", //
      "pad-6-metallic", //
      "pad-7-halo", //
      "pad-8-sweep", //
      "fx-1-rain", //
      "fx-2-soundtrack", //
      "fx-3-crystal", //
      "fx-4-atmosphere", //
      "fx-5-brightness", //
      "fx-6-goblins", //
      "fx-7-echoes", //
      "fx-8-sci-fi", //
      "sitar", //
      "banjo", //
      "shamisen", //
      "koto", //
      "kalimba", //
      "bagpipe", //
      "fiddle", //
      "shanai", //
      "tinkle-bell", //
      "agogo", //
      "steel-drums", //
      "woodblock", //
      "taiko-drum", //
      "melodic-tom", //
      "synth-drum", //
      "reverse-cymbal", //
      "guitar-fret-noise", //
      "breath-noise", //
      "seashore", //
      "bird-tweet", //
      "telephone-ring", //
      "helicopter", //
      "applause", //
      "gunshot", //
  };

  public static int getDrum(String name) {
    return findIndex(DRUMS, name);
  }

  public static String getDrumName(int index) {
    return findElement(DRUMS, index - Midi.DRUM_BASE);
  }

  public static int getInstrument(String name) {
    return findIndex(INSTRUMENTS, name);
  }

  public static String getInstrumentName(int index) {
    return findElement(INSTRUMENTS, index);
  }

  private static String findElement(String[] array, int index) {
    String instrument;
    if (index < 0 || index >= array.length) {
      instrument = Integer.toString(index);
    } else {
      instrument = array[index];
    }
    return instrument;
  }

  private static int findIndex(String[] array, String name) {
    for (int i = 0; i < array.length; i++) {
      if (array[i].equals(name)) {
        return i;
      }
    }
    return -1;
  }
}
