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
      N + "1", // C4
      S + "1", // C#
      N + "2", // D
      S + "2", // D#
      N + "3", // E
      N + "E", // F
      S + "E", // F#
      N + "4", // G
      S + "4", // G#
      N + "5", // A
      S + "5", // A#
      N + "6", // B
      N + "+", // C5
      S + "+", // C#
      N + "7", // D
      S + "7", // D#
      N + "8", // E
      N + "9", // F
      S + "9", // F#
      N + "-", // G
      S + "-", // G#
      N + "N", // A
      S + "N", // A#
      N + "/", // B
      N + "+", // C6
      S + "+", // C#
      N + "B", // D
      S + "B", // D#
  };

  @Override
  public int getDefaultOctave() {
    return 4;
  }

  @Override
  public int getDefaultRange() {
    return 27; // From C4 to D#4
  }

  @Override
  public MappingType getType() {
    return MappingType.NUMERIC;
  }

  @Override
  protected String[] getKeySequence() {
    return KEY_SEQUENCE;
  }

}
