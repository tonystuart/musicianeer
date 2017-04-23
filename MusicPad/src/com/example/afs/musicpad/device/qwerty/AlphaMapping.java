// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import com.example.afs.musicpad.analyzer.Names;

public class AlphaMapping extends QwertyMapping {

  private static final String N = ""; // normal
  private static final String S = "\u21E7"; // sharp (shift)

  private static final String[] KEY_CAPS = new String[] {
      N + "A", // A3
      S + "A", // A#3
      N + "B", // B3
      N + "C", // C4
      S + "C", // C#4
      N + "D", // D4
      S + "D", // D#4
      N + "E", // E4
      N + "F", // F4
      S + "F", // F#4
      N + "G", // G4
      S + "G", // G#4
      N + "H", // A4
      S + "H", // A#4
      N + "I", // B4
      N + "J", // C5
      S + "J", // C#5
      N + "K", // D5
      S + "K", // D#5
      N + "L", // E5
      N + "M", // F5
      S + "M", // F#5
      N + "N", // G5
      S + "N", // G#5
      N + "O", // A5
      S + "O", // A#5
      N + "P", // B5
      N + "Q", // C6
      S + "Q", // C#6
      N + "R", // D6
      S + "R", // D#6
      N + "S", // E6
      N + "T", // F6
      S + "T", // F#6
      N + "U", // G6
      S + "U", // G#6
      N + "V", // A6
      S + "V", // A#6
      N + "W", // B6
      N + "X", // C7
      S + "X", // C#7
      N + "Y", // D7
      S + "Y", // D#7
      N + "Z", // E7
  };

  public static void main(String[] args) {
    char thisChar = 'A' - 1;
    for (int i = 45; i <= 88; i++) {
      int semitone = i % 12;
      boolean isSharp = "101011010101".charAt(semitone) == '0';
      String modifier;
      if (isSharp) {
        modifier = "S + " + "\"" + thisChar + "\"";
      } else {
        thisChar++;
        modifier = "N + " + "\"" + thisChar + "\"";
      }
      System.out.println(modifier + ", // " + Names.formatNoteName(i));
    }
  }

  @Override
  public String toKeyCap(int midiNote) {
    String keyCap;
    if (midiNote >= 45 && midiNote <= 88) {
      keyCap = KEY_CAPS[midiNote - 45];
    } else {
      keyCap = "?";
    }
    return keyCap;
  }

}
