// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

public class NumericMapping extends QwertyMapping {

  private static final int FIRST = 43;
  private static final int LAST = 78;
  public static final int OPTIMUM = 55;

  private static final String[] KEY_CAPS = new String[] {
      "-2", // G (FIRST)
      "/2", // G#
      "-3", // A
      "/3", // A#
      "-4", // B
      "-5", // C4 (48)
      "/5", // C#
      "-6", // D
      "/6", // D#
      "-7", // E
      "-8", // F
      "/8", // F#
      //
      "2", // G 55 (OPTIMUM)
      "#2", // G# 56
      "3", // A 57
      "#3", // A# 58
      "4", // B 59
      "5", // C5 60
      "#5", // C# 61
      "6", // D 62
      "#6", // D# 63
      "7", // E 64
      "8", // F 65
      "#8", // F# 66
      //
      "+2", // G 67
      "*2", // G# 68
      "+3", // A 69
      "*3", // A# 70
      "+4", // B 71
      "+5", // C6 72
      "*5", // C# 73
      "+6", // D 74
      "*6", // D# 75
      "+7", // E 76
      "+8", // F 77
      "*8", // F# 78 (LAST)
  };

  @Override
  public String toKeyCap(int midiNote) {
    String keyCap;
    if (midiNote >= FIRST && midiNote <= LAST) {
      keyCap = KEY_CAPS[midiNote - FIRST];
    } else {
      keyCap = "?";
    }
    return keyCap;
  }

}
