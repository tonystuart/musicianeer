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

import com.example.afs.musicpad.midi.Midi;

public class IntervalSet {

  public class RootIntervals {
    private int root;
    private int[] intervals;

    public RootIntervals(int root, int[] intervals) {
      this.root = root;
      this.intervals = intervals;
    }

    public int[] getIntervals() {
      return intervals;
    }

    public int getRoot() {
      return root;
    }

    @Override
    public String toString() {
      return "RootIntervals [root=" + root + ", intervals=" + Arrays.toString(intervals) + "]";
    }

  }

  private int lowestMidiNote = Midi.MAX_VALUE;
  private boolean[] commonNotes = new boolean[Midi.SEMITONES_PER_OCTAVE];

  public void add(int midiNote) {
    if (midiNote < lowestMidiNote) {
      lowestMidiNote = midiNote;
    }
    commonNotes[midiNote % Midi.SEMITONES_PER_OCTAVE] = true;
  }

  public SoundType getSoundType() {
    RootIntervals[] rootIntervals = getRootIntervals();
    for (int i = 0; i < Intervals.INTERVALS.length; i++) {
      Intervals targetIntervals = Intervals.INTERVALS[i];
      for (int j = 0; j < rootIntervals.length; j++) {
        RootIntervals sourceIntervals = rootIntervals[j];
        if (targetIntervals.matches(sourceIntervals.getIntervals())) {
          SoundType soundType = new SoundType(sourceIntervals.getRoot(), targetIntervals);
          return soundType;
        }
      }
    }
    RootIntervals lowestIntervals = findLowestIntervals(rootIntervals);
    SoundType soundType = createSoundType(lowestIntervals);
    return soundType;
  }

  public int[][] getIntervals() {
    int[][] inversions = getInversions();
    int inversionCount = inversions.length;
    int noteCount = inversionCount;
    int[][] intervals = new int[inversionCount][noteCount];
    for (int i = 0; i < inversionCount; i++) {
      intervals[i][0] = 0;
      int root = inversions[i][0];
      for (int j = 1; j < noteCount; j++) {
        intervals[i][j] = ((inversions[i][j] + Midi.SEMITONES_PER_OCTAVE) - root) % Midi.SEMITONES_PER_OCTAVE;
      }
    }
    return intervals;
  }

  public int[][] getInversions() {
    int noteCount = 0;
    for (int i = 0; i < commonNotes.length; i++) {
      if (commonNotes[i]) {
        noteCount++;
      }
    }
    int next = 0;
    int[] sortedNotes = new int[noteCount];
    for (int i = 0; i < commonNotes.length; i++) {
      if (commonNotes[i]) {
        sortedNotes[next++] = i;
      }
    }
    int[][] inversions = new int[noteCount][noteCount];
    for (int i = 0; i < noteCount; i++) {
      for (int j = 0; j < noteCount; j++) {
        inversions[i][j] = sortedNotes[(i + j) % sortedNotes.length];
      }
    }
    return inversions;
  }

  public RootIntervals[] getRootIntervals() {
    int[][] inversions = getInversions();
    int inversionCount = inversions.length;
    RootIntervals[] rootIntervals = new RootIntervals[inversionCount];
    int noteCount = inversionCount;
    for (int i = 0; i < inversionCount; i++) {
      int[] intervals = new int[noteCount];
      intervals[0] = 0;
      int root = inversions[i][0];
      for (int j = 1; j < noteCount; j++) {
        intervals[j] = ((inversions[i][j] + Midi.SEMITONES_PER_OCTAVE) - root) % Midi.SEMITONES_PER_OCTAVE;
      }
      rootIntervals[i] = new RootIntervals(root, intervals);
    }
    return rootIntervals;
  }

  private SoundType createSoundType(RootIntervals rootIntervals) {
    StringBuilder s = new StringBuilder();
    for (int i = 1; i < rootIntervals.getIntervals().length; i++) {
      int interval = rootIntervals.getIntervals()[i] - rootIntervals.getIntervals()[0];
      s.append("+");
      s.append(Intervals.intervalNames[interval]);
    }
    Intervals intervals = new Intervals(s.toString(), rootIntervals.getIntervals());
    SoundType soundType = new SoundType(rootIntervals.getRoot(), intervals);
    return soundType;
  }

  private RootIntervals findLowestIntervals(RootIntervals[] rootIntervals) {
    for (int i = 0; i < rootIntervals.length; i++) {
      if (lowestMidiNote % Midi.SEMITONES_PER_OCTAVE == rootIntervals[i].getRoot()) {
        return rootIntervals[i];
      }
    }
    return null;
  }

}