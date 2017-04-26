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

  private static final String[] KEY_SEQUENCE = new String[] {
      "1", // C
      "2", // C#
      "3", // D
      "E", // D#
      "4", // E
      "5", // F
      "6", // F#
      "+", // G
      "7", // G#
      "8", // A
      "9", // A#
      "-", // B
      "N", // C
      "/", // C#
      "+", // D
      "B", // D#
  };

  @Override
  public int getBaseOctave() {
    return 4;
  }

  @Override
  protected String[] getKeySequence() {
    return KEY_SEQUENCE;
  }

}
