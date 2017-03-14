// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.theory;

public class Key {

  private String name;
  private int tonic;
  private int octaveCount;
  private Scale scale;
  private int[] midiNotes;

  public Key(String name, int tonic, int octaveCount, Scale scale) {
    this.name = name;
    this.tonic = tonic;
    this.octaveCount = octaveCount;
    this.scale = scale;
    int noteCount = octaveCount * 7;
    this.midiNotes = new int[noteCount];
    for (int i = 0, value = tonic; i < noteCount; i++) {
      midiNotes[i] = value;
      int halfStepsToNextNote = scale.getInterval(i);
      value += halfStepsToNextNote;
    }
  }

  public int[] getMidiNotes() {
    return midiNotes;
  }

  public String getName() {
    return name;
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
