// Copyright 2007 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import java.util.Arrays;

import com.example.afs.musicpad.analyzer.BeatFinder.BeatNote;
import com.example.afs.musicpad.analyzer.BeatFinder.BeatNotes;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;

public class Names {

  private static class ChordName {
    private String intervals;
    private String name;

    private ChordName(String intervals, String name) {
      this.intervals = intervals;
      this.name = name;
    }

    public String getIntervals() {
      return intervals;
    }

    public String getName() {
      return name;
    }
  }

  private static final String[] SHARPS = new String[] {
      "C",
      "C#",
      "D",
      "D#",
      "E",
      "F",
      "F#",
      "G",
      "G#",
      "A",
      "A#",
      "B"
  };

  private static final String[] FLATS = new String[] {
      "C",
      "Db",
      "D",
      "Eb",
      "E",
      "F",
      "Gb",
      "G",
      "Ab",
      "A",
      "Bb",
      "B"
  };

  private final static String intervalNames[] = new String[] //
  { //
      "oct", // 0
      "min2", // 1
      "maj2", // 2
      "min3", // 3
      "maj3", // 4
      "perf4", // 5
      "dim5", // 6
      "perf5", // 7
      "aug5", // 8
      "maj6", // 9
      "min7", // 10
      "maj7", // 11
  };
  private final static ChordName chordNames[] = new ChordName[] //
  { //               012345678901
      new ChordName("100100010000", "min"), //
      new ChordName("100100100000", "dim"), //
      new ChordName("100010010000", "Maj"), //
      new ChordName("100010001000", "aug"), //
      new ChordName("100001010000", "sus4"), //
      new ChordName("100010010010", "7"), //
      new ChordName("100010010001", "Maj7"), //
      new ChordName("100100010010", "min7"), //
      new ChordName("100010010100", "Maj6"), //
      new ChordName("100100010100", "m6"), //
      new ChordName("100000010000", "5"), //
  };

  public static String formatBeatNotes(BeatNotes beatNotes) {
    StringBuilder s = new StringBuilder();
    s.append("[");
    if (beatNotes.size() > 0) {
      BeatNote root = beatNotes.first();
      char intervals[] = new char[Midi.SEMITONES_PER_OCTAVE];
      Arrays.fill(intervals, '0');
      String rootNoteName = null;
      for (BeatNote beatNote : beatNotes) {
        int commonNote = beatNote.getCommonNote();
        int interval = commonNote - root.getCommonNote();
        String noteName = Names.getNoteName(commonNote);
        String intervalName = Names.getIntervalName(interval);
        if (rootNoteName == null) {
          rootNoteName = noteName;
        } else {
          s.append("+");
        }
        s.append(noteName + "(" + intervalName + ")");
        intervals[interval] = '1';
      }
      String chordName = getChordName(intervals);
      if (chordName != null) {
        s.append("=" + rootNoteName + chordName);
      }
    }
    s.append("]");
    return s.toString();
  }

  public static String formatDrum(int midiNote) {
    return Instruments.getDrumName(midiNote) + " (" + midiNote + ")";
  }

  public static String formatDrumName(int midiNote) {
    return Instruments.getDrumName(midiNote);
  }

  public static String formatNote(int midiNote) {
    return SHARPS[midiNote % Midi.SEMITONES_PER_OCTAVE] + (midiNote / Midi.SEMITONES_PER_OCTAVE) + " (" + midiNote + ")";
  }

  public static String formatNote(long tick, int midiNote, long duration) {
    return tick + " " + formatNote(midiNote) + " " + duration;
  }

  public static String formatNoteName(int midiNote) {
    return SHARPS[midiNote % Midi.SEMITONES_PER_OCTAVE] + (midiNote / Midi.SEMITONES_PER_OCTAVE);
  }

  public static String getIntervalName(int interval) {
    return intervalNames[interval];
  }

  public static String getKeyName(int tonic, boolean isMajor, int sharpsOrFlats) {
    String midiNote = (sharpsOrFlats < 0 ? FLATS[tonic] : SHARPS[tonic]);
    String mode = isMajor ? " Major" : " minor";
    String key = midiNote + mode;
    return key;
  }

  public static String getNoteName(int midiNote) {
    return SHARPS[midiNote % Midi.SEMITONES_PER_OCTAVE];
  }

  public static String getSynopsis(int sharpsOrFlats) {
    String nickName;
    if (sharpsOrFlats < 0) {
      nickName = getPlural(-sharpsOrFlats, "flat");
    } else if (sharpsOrFlats > 0) {
      nickName = getPlural(sharpsOrFlats, "sharp");
    } else {
      nickName = "";
    }
    return nickName;
  }

  private static String getChordName(char[] intervals) {
    String intervalString = new String(intervals);
    for (ChordName chordName : chordNames) {
      if (chordName.getIntervals().equals(intervalString)) {
        return chordName.getName();
      }
    }
    return null;
  }

  private static String getPlural(int number, String text) {
    String plural;
    if (number == 0) {
      plural = "no " + text + "s";
    } else if (number > 1) {
      plural = number + " " + text + "s";
    } else {
      plural = "1 " + text;
    }
    return plural;
  }

}
