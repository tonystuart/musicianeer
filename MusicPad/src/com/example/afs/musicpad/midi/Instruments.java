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
      "Acoustic Grand Piano", //
      "Bright Acoustic Piano", //
      "Electric Grand Piano", //
      "Honky Tonk Piano", //
      "Electric Piano 1", //
      "Electric Piano 2", //
      "Harpsichord", //
      "Clavinet", //
      "Celesta", //
      "Glockenspiel", //
      "Music Box", //
      "Vibraphone", //
      "Marimba", //
      "Xylophone", //
      "Tubular Bells", //
      "Dulcimer", //
      "Drawbar Organ", //
      "Percussive Organ", //
      "Rock Organ", //
      "Church Organ", //
      "Reed Organ", //
      "Accordion", //
      "Harmonica", //
      "Tango Accordion", //
      "Acoustic Guitar Nylon", //
      "Acoustic Guitar Steel", //
      "Electric Guitar Jazz", //
      "Electric Guitar Clean", //
      "Electric Guitar Muted", //
      "Overdriven Guitar", //
      "Distortion Guitar", //
      "Guitar Harmonics", //
      "Acoustic Bass", //
      "Electric Bass Finger", //
      "Electric Bass Pick", //
      "Fretless Bass", //
      "Slap Bass 1", //
      "Slap Bass 2", //
      "Synth Bass 1", //
      "Synth Bass 2", //
      "Violin", //
      "Viola", //
      "Cello", //
      "Contrabass", //
      "Tremolo Strings", //
      "Pizzicato Strings", //
      "Orchestral Harp", //
      "Timpani", //
      "String Ensemble 1", //
      "String Ensemble 2", //
      "Synth Strings 1", //
      "Synth Strings 2", //
      "Choir Aahs", //
      "Voice Oohs", //
      "Synth Choir", //
      "Orchestra Hit", //
      "Trumpet", //
      "Trombone", //
      "Tuba", //
      "Muted Trumpet", //
      "French Horn", //
      "Brass Section", //
      "Synth Brass 1", //
      "Synth Brass 2", //
      "Soprano Sax", //
      "Alto Sax", //
      "Tenor Sax", //
      "Baritone Sax", //
      "Oboe", //
      "English Horn", //
      "Bassoon", //
      "Clarinet", //
      "Piccolo", //
      "Flute", //
      "Recorder", //
      "Pan Flute", //
      "Blown Bottle", //
      "Shakuhachi", //
      "Whistle", //
      "Ocarina", //
      "Lead 1 Square", //
      "Lead 2 Sawtooth", //
      "Lead 3 Calliope", //
      "Lead 4 Chiff", //
      "Lead 5 Charang", //
      "Lead 6 Voice", //
      "Lead 7 Fifths", //
      "Lead 8 Bass + Lead", //
      "Pad 1 New Age", //
      "Pad 2 Warm", //
      "Pad 3 Polysynth", //
      "Pad 4 Choir", //
      "Pad 5 Bowed", //
      "Pad 6 Metallic", //
      "Pad 7 Halo", //
      "Pad 8 Sweep", //
      "Fx 1 Rain", //
      "Fx 2 Soundtrack", //
      "Fx 3 Crystal", //
      "Fx 4 Atmosphere", //
      "Fx 5 Brightness", //
      "Fx 6 Goblins", //
      "Fx 7 Echoes", //
      "Fx 8 Sci Fi", //
      "Sitar", //
      "Banjo", //
      "Shamisen", //
      "Koto", //
      "Kalimba", //
      "Bagpipe", //
      "Fiddle", //
      "Shanai", //
      "Tinkle Bell", //
      "Agogo", //
      "Steel Drums", //
      "Woodblock", //
      "Taiko Drum", //
      "Melodic Tom", //
      "Synth Drum", //
      "Reverse Cymbal", //
      "Guitar Fret Noise", //
      "Breath Noise", //
      "Seashore", //
      "Bird Tweet", //
      "Telephone Ring", //
      "Helicopter", //
      "Applause", //
      "Gunshot", //
  };

  private static final String[] CATEGORIES = new String[] {
      "Piano", //
      "Chromatic Percussion", //
      "Organ", //
      "Guitar", //
      "Bass", //
      "Strings", //
      "Ensemble", //
      "Brass", //
      "Reed", //
      "Pipe", //
      "Synth Lead", //
      "Synth Pad", //
      "Synth Effects", //
      "Ethnic", //
      "Percussive", //
      "Sound Effects" //
  };

  public static String getCategoryName(int category) {
    return findElement(CATEGORIES, category);
  }

  public static int getDrum(String name) {
    return findIndex(DRUMS, name);
  }

  public static int getDrumCount() {
    return DRUMS.length;
  }

  public static String getDrumName(int midiNote) {
    return findElement(DRUMS, midiNote - Midi.DRUM_BASE);
  }

  public static int getInstrument(String name) {
    return findIndex(INSTRUMENTS, name);
  }

  public static String getProgramName(int program) {
    return findElement(INSTRUMENTS, program);
  }

  private static String findElement(String[] array, int arrayIndex) {
    String instrument;
    if (arrayIndex < 0 || arrayIndex >= array.length) {
      instrument = Integer.toString(arrayIndex);
    } else {
      instrument = array[arrayIndex];
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
