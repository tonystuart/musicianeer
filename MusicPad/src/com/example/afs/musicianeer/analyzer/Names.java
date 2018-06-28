// Copyright 2007 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.analyzer;

import java.util.List;

import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Note;

public class Names {

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

  public static String formatDrum(int midiNote) {
    return Instruments.getDrumName(midiNote) + " (" + midiNote + ")";
  }

  public static String formatDrumName(int midiNote) {
    return Instruments.getDrumName(midiNote);
  }

  public static String formatName(List<Note> notes) {
    String name;
    int noteCount = notes.size();
    if (noteCount == 0) {
      name = "";
    } else if (noteCount == 1) {
      name = getNoteName(notes.get(0).getMidiNote());
    } else {
      // search list of intervals for first match
      name = "";
    }
    return name;
  }

  public static String formatNote(int midiNote) {
    return SHARPS[midiNote % Midi.SEMITONES_PER_OCTAVE] + getOctave(midiNote) + " (" + midiNote + ")";
  }

  public static String formatNote(long tick, int midiNote, long duration) {
    return tick + " " + formatNote(midiNote) + " " + duration;
  }

  public static String formatNoteName(int midiNote) {
    return SHARPS[midiNote % Midi.SEMITONES_PER_OCTAVE] + getOctave(midiNote);
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

  private static int getOctave(int midiNote) {
    // Octave is represented in standard pitch notation
    // https://en.wikipedia.org/wiki/Scientific_pitch_notation
    return (midiNote / Midi.SEMITONES_PER_OCTAVE) - 1;
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
