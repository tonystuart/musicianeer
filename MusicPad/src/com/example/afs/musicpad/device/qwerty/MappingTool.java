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

public class MappingTool {

  public static void alphaMapping() {
    int midiNote = 0;
    String hasSharp = "1101110";
    String alpha = "ZXCVBNMASDFGHJKLQWERTYUIOP";
    for (int i = 0; i < alpha.length(); i++) {
      char thisChar = alpha.charAt(i);
      if (hasSharp.charAt(i % hasSharp.length()) == '1') {
        System.out.println("N + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
        System.out.println("S + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
      } else {
        System.out.println("N + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
      }
    }
  }

  public static void main(String[] args) {
    numericMapping();
  }

  public static void numericMapping() {
    int midiNote = 0;
    String hasSharp = "1101110";
    String numeric = "1234567";
    for (int i = 0; i < numeric.length(); i++) {
      char thisChar = numeric.charAt(i);
      if (hasSharp.charAt(i % hasSharp.length()) == '1') {
        System.out.println("LN + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
        System.out.println("LS + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
      } else {
        System.out.println("LN + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
      }
    }
    for (int i = 0; i < numeric.length(); i++) {
      char thisChar = numeric.charAt(i);
      if (hasSharp.charAt(i % hasSharp.length()) == '1') {
        System.out.println("MN + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
        System.out.println("MS + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
      } else {
        System.out.println("MN + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
      }
    }
    for (int i = 0; i < numeric.length(); i++) {
      char thisChar = numeric.charAt(i);
      if (hasSharp.charAt(i % hasSharp.length()) == '1') {
        System.out.println("HN + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
        System.out.println("HS + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
      } else {
        System.out.println("HN + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
      }
    }
  }

  public static void qwertyMapping() {
    String cMajor = "101011010101";
    String alpha = "ZXCVBNMASDFGHJKLQWERTYUIOP";
    toMidiNote(cMajor, alpha);
    String numeric = "123456789";
    toMidiNote(cMajor, numeric);
  }

  public static void toMidiNote(String cMajor, String alpha) {
    int letter = 0;
    int midiNote = 0;
    while (letter < alpha.length()) {
      if (cMajor.charAt(midiNote % cMajor.length()) == '1') {
        System.out.println("case '" + alpha.charAt(letter) + "': semitone = " + midiNote + "; // " + Names.getNoteName(midiNote) + "\nbreak;");
        letter++;
      }
      midiNote++;
    }
  }

}
