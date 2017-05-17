// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.theory;

import java.util.Arrays;

import com.example.afs.musicpad.midi.Midi;

public class Scale {

  private String name;
  private int[] intervals;
  private int[] fullToneToSemiTone = new int[Midi.NOTES_PER_OCTAVE];

  public Scale(String name, int... intervals) {
    this.name = name;
    this.intervals = intervals;
    for (int i = 0, value = 0; i < fullToneToSemiTone.length; i++) {
      fullToneToSemiTone[i] = value;
      int halfStepsToNextNote = intervals[i];
      value += halfStepsToNextNote;
    }
  }

  public int getInterval(int degree) {
    return intervals[degree % intervals.length];
  }

  public int[] getIntervals() {
    return intervals;
  }

  public String getName() {
    return name;
  }

  public int getSemiTone(int fullTone) {
    return fullToneToSemiTone[fullTone];
  }

  @Override
  public String toString() {
    return "Scale [name=" + name + ", intervals=" + Arrays.toString(intervals) + "]";
  }

}
