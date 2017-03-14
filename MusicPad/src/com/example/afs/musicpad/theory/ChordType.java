// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.theory;

import java.util.Arrays;

import com.example.afs.musicpad.analyzer.Names;

public class ChordType implements Comparable<ChordType> {

  private String name;
  private int[] midiNotes;

  public ChordType(int root, Intervals intervals) {
    name = Names.getNoteName(root) + intervals.getName();
    int[] intervalArray = intervals.getIntervals();
    midiNotes = new int[intervalArray.length];
    for (int i = 0; i < midiNotes.length; i++) {
      int midiNote = root + intervalArray[i];
      midiNotes[i] = midiNote;
    }
  }

  public ChordType(String name, int... midiNotes) {
    this.name = name;
    this.midiNotes = midiNotes;
  }

  @Override
  public int compareTo(ChordType that) {
    int limit = Math.min(this.midiNotes.length, that.midiNotes.length);
    for (int i = 0; i < limit; i++) {
      int deltaSemitone = this.midiNotes[i] - that.midiNotes[i];
      if (deltaSemitone != 0) {
        return deltaSemitone;
      }
    }
    int deltaLength = this.midiNotes.length - that.midiNotes.length;
    if (deltaLength != 0) {
      return deltaLength;
    }
    return 0;
  }

  public int getLength() {
    return midiNotes.length;
  }

  public int[] getMidiNotes() {
    return midiNotes;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "ChordType [name=" + name + ", midiNotes=" + Arrays.toString(midiNotes) + "]";
  }

}