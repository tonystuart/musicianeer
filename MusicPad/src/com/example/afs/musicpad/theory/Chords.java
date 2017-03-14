// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.theory;

import java.util.LinkedList;
import java.util.List;

import com.example.afs.musicpad.midi.Midi;

public class Chords {

  private static final Intervals[] INTERVALS = new Intervals[] {
      Intervals.MAJOR_NINTH,
      Intervals.MINOR_NINTH,
      Intervals.MAJOR_SEVENTH,
      Intervals.MINOR_SEVENTH,
      Intervals.SEVENTH,
      Intervals.MAJOR,
      Intervals.MINOR,
      Intervals.AUGMENTED,
      Intervals.DIMINISHED,
  };

  private static final List<ChordType> CHORD_TYPES = createChordTypes();

  public static List<ChordType> createChordTypes() {
    List<ChordType> chordTypes = new LinkedList<>();
    for (int root = 0; root < Midi.SEMITONES_PER_OCTAVE; root++) {
      for (Intervals intervals : INTERVALS) {
        ChordType chordType = new ChordType(root, intervals);
        chordTypes.add(chordType);
      }
    }
    return chordTypes;
  }

  public static List<ChordType> getChords() {
    return CHORD_TYPES;
  }

}