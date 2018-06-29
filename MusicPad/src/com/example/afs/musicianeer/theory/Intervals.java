// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.theory;

import java.util.Arrays;

import com.example.afs.musicianeer.midi.Midi;

public class Intervals {

  public final static String intervalNames[] = new String[] //
  { //
      "oct", // 0
      "min2", // 1
      "Maj2", // 2
      "min3", // 3
      "Maj3", // 4
      "perf4", // 5
      "dim5", // 6
      "perf5", // 7
      "aug5", // 8
      "Maj6", // 9
      "min7", // 10
      "Maj7", // 11
  };

  // https://en.wikipedia.org/wiki/Chord_(music)
  // https://en.wikipedia.org/wiki/Half-diminished_seventh_chord

  public static final Intervals ADD_TWO = new Intervals("Add2", 0, 2, 4, 7);
  public static final Intervals SUSPENDED_SECOND = new Intervals("susp2", 0, 2, 7);
  public static final Intervals SEVENTH_SUSPENDED_SECOND = new Intervals("7susp2", 0, 2, 7, 10);

  public static final Intervals DIMINISHED = new Intervals("dim", 0, 3, 6);
  public static final Intervals DIMINISHED_SEVENTH = new Intervals("dim7", 0, 3, 6, 9);
  public static final Intervals HALF_DIMINISHED = new Intervals("m7b5", 0, 3, 6, 10);
  public static final Intervals MINOR = new Intervals("min", 0, 3, 7);
  public static final Intervals MINOR_SIXTH = new Intervals("min7", 0, 3, 7, 9);
  public static final Intervals MINOR_SEVENTH = new Intervals("min7", 0, 3, 7, 10);
  public static final Intervals MINOR_NINTH = new Intervals("min9", 0, 3, 7, 10, 14);
  public static final Intervals MINOR_ELEVENTH = new Intervals("min11", 0, 3, 7, 10, 14, 17);
  public static final Intervals MINOR_THIRTEENTH = new Intervals("min13", 0, 3, 7, 10, 14, 17, 21);
  public static final Intervals MINOR_MAJOR_SEVENTH = new Intervals("mM7", 0, 3, 7, 11);

  public static final Intervals ADD_FOURTH = new Intervals("Add4", 0, 4, 5, 7);
  public static final Intervals SEVENTH_FLAT_FIFTH = new Intervals("M7b5", 0, 4, 6, 10);
  public static final Intervals MAJOR = new Intervals("Maj", 0, 4, 7);
  public static final Intervals SIXTH = new Intervals("6", 0, 4, 7, 9);
  public static final Intervals SIX_NINE = new Intervals("6/9", 0, 4, 7, 9, 14);
  public static final Intervals SEVENTH = new Intervals("7", 0, 4, 7, 10);
  public static final Intervals SEVENTH_FLAT_NINE = new Intervals("M7b9", 0, 4, 7, 10, 13);
  public static final Intervals SEVENTH_SHARP_NINE = new Intervals("M7#9", 0, 4, 7, 10, 15);
  public static final Intervals NINTH = new Intervals("9", 0, 4, 7, 10, 14);
  public static final Intervals ELEVENTH = new Intervals("11", 0, 4, 7, 10, 14, 17);
  public static final Intervals THIRTEENTH = new Intervals("13", 0, 4, 7, 10, 14, 17, 21);
  public static final Intervals ADD_NINE = new Intervals("Add9", 0, 4, 7, 14);
  public static final Intervals MAJOR_SEVENTH = new Intervals("Maj7", 0, 4, 7, 11);
  public static final Intervals MAJOR_NINTH = new Intervals("Maj9", 0, 4, 7, 11, 14);
  public static final Intervals MAJOR_ELEVENTH = new Intervals("Maj11", 0, 4, 7, 11, 14, 17);
  public static final Intervals MAJOR_THIRTEENTH = new Intervals("Maj13", 0, 4, 7, 11, 14, 17, 21);
  public static final Intervals AUGMENTED = new Intervals("aug", 0, 4, 8);
  public static final Intervals SEVENTH_SHARP_FIFTH = new Intervals("M7#5", 0, 4, 8, 10);

  public static final Intervals SUSPENDED_FOURTH = new Intervals("susp4", 0, 5, 7);
  public static final Intervals SEVENTH_SUSPENDED_FOURTH = new Intervals("7susp4", 0, 5, 7, 10);

  public static final Intervals FIFTH = new Intervals("5", 0, 7);

  public static final Intervals[] INTERVALS = new Intervals[] {

      // 2 note intervals
      FIFTH,

      // 3 note chords
      MAJOR,
      MINOR,
      AUGMENTED,
      DIMINISHED,
      SUSPENDED_SECOND,
      SUSPENDED_FOURTH,

      // 4 note chords
      SEVENTH,
      MAJOR_SEVENTH,
      MINOR_SEVENTH,
      DIMINISHED_SEVENTH,
      SEVENTH_SUSPENDED_SECOND,
      SEVENTH_SUSPENDED_FOURTH,
      SEVENTH_FLAT_FIFTH,
      SEVENTH_SHARP_FIFTH,
      SIXTH,
      MINOR_SIXTH,
      HALF_DIMINISHED,
      MINOR_MAJOR_SEVENTH,
      ADD_TWO,
      ADD_FOURTH,
      ADD_NINE,

      // 5 note chords
      NINTH,
      MAJOR_NINTH,
      MINOR_NINTH,
      SIX_NINE,
      SEVENTH_FLAT_NINE,
      SEVENTH_SHARP_NINE,

      // 6 note chords
      ELEVENTH,
      MAJOR_ELEVENTH,
      MINOR_ELEVENTH,

      // 7 note chords
      THIRTEENTH,
      MAJOR_THIRTEENTH,
      MINOR_THIRTEENTH,

  };

  private String name;
  private int[] intervals;

  public Intervals(String name, int... intervals) {
    this.name = name;
    this.intervals = new int[intervals.length];
    for (int i = 0; i < intervals.length; i++) {
      this.intervals[i] = intervals[i] % Midi.SEMITONES_PER_OCTAVE;
    }
  }

  public int[] getIntervals() {
    return intervals;
  }

  public String getName() {
    return name;
  }

  public boolean matches(int[] intervals) {
    return Arrays.equals(this.intervals, intervals);
  }

  @Override
  public String toString() {
    return "Intervals [name=" + name + ", intervals=" + Arrays.toString(intervals) + "]";
  }

}
