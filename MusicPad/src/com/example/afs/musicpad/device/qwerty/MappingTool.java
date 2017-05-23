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

  public static void main(String[] args) {
    mapAlpha();
    mapNumeric();
  }

  public static void mapAlpha() {
    String cMajor = "101011010101";
    String sequence = "ZXCVBNMASDFGHJKLQWERTYUIOP[]"; // must be integral multiple of 7 or math on its length won't work
    doChromaticScale(sequence);
    toMidiNote(cMajor, sequence);
  }

  public static void mapNumeric() {
    String cMajor = "101011010101";
    String sequence = "123456789";
    doChromaticScale(sequence);
    toMidiNote(cMajor, sequence);
  }

  public static void numericMapping() {
    doChromaticScale("123E456+789-N/+B");
  }

  public static void qwertyMapping() {
    String cMajor = "101011010101";
    String alpha = "ZXCVBNMASDFGHJKLQWERTYUIOP";
    toMidiNote(cMajor, alpha);
    String numeric = "123E456+789-N/*B";
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

  private static void doChromaticScale(String legend) {
    int midiNote = 0;
    String hasSharp = "1101110";
    for (int i = 0; i < legend.length(); i++) {
      char thisChar = legend.charAt(i);
      if (hasSharp.charAt(i % hasSharp.length()) == '1') {
        System.out.println("N + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
        System.out.println("S + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
      } else {
        System.out.println("N + " + "\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
      }
    }
  }

  @SuppressWarnings("unused")
  private static void doWholeNoteScale(String legend) {
    int midiNote = 0;
    for (int i = 0; i < legend.length(); i++) {
      char thisChar = legend.charAt(i);
      System.out.println("\"" + thisChar + "\", // " + Names.getNoteName(midiNote++));
    }
  }

}
