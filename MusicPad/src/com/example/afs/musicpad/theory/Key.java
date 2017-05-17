// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.theory;

import com.example.afs.musicpad.midi.Midi;

public class Key {

  private String name;
  private int tonic;
  private int octaveCount;
  private Scale scale;

  public Key(String name, int tonic, int octaveCount, Scale scale) {
    this.name = name;
    this.tonic = tonic;
    this.octaveCount = octaveCount;
    this.scale = scale;
  }

  public String getName() {
    return name;
  }

  public int getNoteInKey(int fulltone) {
    int base = fulltone - tonic;
    int octave = base / Midi.NOTES_PER_OCTAVE;
    int offset = octave % Midi.NOTES_PER_OCTAVE;
    int midiNote = octave * Midi.SEMITONES_PER_OCTAVE + scale.getSemiTone(offset);
    return midiNote;
  }

  public int getOctaveCount() {
    return octaveCount;
  }

  public Scale getScale() {
    return scale;
  }

  public int getTonic() {
    return tonic;
  }

  @Override
  public String toString() {
    return "Key [name=" + name + ", tonic=" + tonic + ", octaveCount=" + octaveCount + ", scale=" + scale + "]";
  }

}
