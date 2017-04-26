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

  private static final String LN = "-"; // low normal
  private static final String LS = "/"; // low sharp

  private static final String MN = ""; // middle normal
  private static final String MS = "#"; // middle sharp

  private static final String HN = "+"; // high normal
  private static final String HS = "*"; // high sharp

  private static final String[] KEY_SEQUENCE = new String[] {
      LN + "1", // C
      LS + "1", // C#
      LN + "2", // D
      LS + "2", // D#
      LN + "3", // E
      LN + "4", // F
      LS + "4", // F#
      LN + "5", // G
      LS + "5", // G#
      LN + "6", // A
      LS + "6", // A#
      LN + "7", // B
      MN + "1", // C
      MS + "1", // C#
      MN + "2", // D
      MS + "2", // D#
      MN + "3", // E
      MN + "4", // F
      MS + "4", // F#
      MN + "5", // G
      MS + "5", // G#
      MN + "6", // A
      MS + "6", // A#
      MN + "7", // B
      HN + "1", // C
      HS + "1", // C#
      HN + "2", // D
      HS + "2", // D#
      HN + "3", // E
      HN + "4", // F
      HS + "4", // F#
      HN + "5", // G
      HS + "5", // G#
      HN + "6", // A
      HS + "6", // A#
      HN + "7", // B
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
