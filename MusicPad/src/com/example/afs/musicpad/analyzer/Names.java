// Copyright 2007 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import com.example.afs.musicpad.song.Instruments;

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

  public static String formatDrum(int noteNumber) {
    return Instruments.getDrumName(noteNumber) + " (" + noteNumber + ")";
  }

  public static String formatDrumName(int noteNumber) {
    return Instruments.getDrumName(noteNumber);
  }

  public static String formatNote(int note) {
    return SHARPS[note % 12] + (note / 12) + " (" + note + ")";
  }

  public static String formatNote(long tick, int note, long duration) {
    return tick + " " + formatNote(note) + " " + duration;
  }

  public static String formatNoteName(int note) {
    return SHARPS[note % 12] + (note / 12);
  }

  public static String getKeyName(int tonic, boolean isMajor, int sharpsOrFlats) {
    String note = (sharpsOrFlats < 0 ? FLATS[tonic] : SHARPS[tonic]);
    String mode = isMajor ? " Major" : " minor";
    String key = note + mode;
    return key;
  }

  public static String getNoteName(int note) {
    return SHARPS[note % 12];
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
