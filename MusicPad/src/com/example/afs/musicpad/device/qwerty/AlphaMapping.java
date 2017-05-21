// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

public class AlphaMapping extends QwertyMapping {

  private static final String[] KEY_SEQUENCE = new String[] {
      N + "Z", // C3
      S + "Z", // C#
      N + "X", // D
      S + "X", // D#
      N + "C", // E
      N + "V", // F
      S + "V", // F#
      N + "B", // G
      S + "B", // G#
      N + "N", // A
      S + "N", // A#
      N + "M", // B
      N + "A", // C4
      S + "A", // C#
      N + "S", // D
      S + "S", // D#
      N + "D", // E
      N + "F", // F
      S + "F", // F#
      N + "G", // G
      S + "G", // G#
      N + "H", // A
      S + "H", // A#
      N + "J", // B
      N + "K", // C5
      S + "K", // C#
      N + "L", // D
      S + "L", // D#
      N + "Q", // E
      N + "W", // F
      S + "W", // F#
      N + "E", // G
      S + "E", // G#
      N + "R", // A
      S + "R", // A#
      N + "T", // B
      N + "Y", // C6
      S + "Y", // C#
      N + "U", // D
      S + "U", // D#
      N + "I", // E
      N + "O", // F
      S + "O", // F#
      N + "P", // G
      S + "P", // G#
  };

  @Override
  public int getDefaultOctave() {
    return 3;
  }

  @Override
  public int getDefaultRange() {
    return 44; // from C3 to G#6
  }

  @Override
  public InputType getType() {
    return InputType.ALPHA;
  }

  @Override
  protected String[] getKeySequence() {
    return KEY_SEQUENCE;
  }

}
