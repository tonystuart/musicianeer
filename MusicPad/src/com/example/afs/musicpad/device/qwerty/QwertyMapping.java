// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import com.example.afs.musicpad.device.common.InputMapping;

public abstract class QwertyMapping implements InputMapping {

  // http://www.fileformat.info/
  protected static final int ENTER = '\u23ce';
  protected static final int BACK_SPACE = '\u2190';
  protected static final int NUM_LOCK = '#';

  @Override
  public int toMidiNote(int inputCode) {
    int noteIndex;
    switch (inputCode) {
    case 'A':
      noteIndex = 45;
      break;
    case 'B':
      noteIndex = 47;
      break;
    case 'C':
      noteIndex = 48;
      break;
    case 'D':
      noteIndex = 50;
      break;
    case 'E':
      noteIndex = 52;
      break;
    case 'F':
      noteIndex = 53;
      break;
    case 'G':
      noteIndex = 55;
      break;
    case 'H': // A
      noteIndex = 57;
      break;
    case 'I': // B
      noteIndex = 59;
      break;
    case 'J': // C
      noteIndex = 60;
      break;
    case 'K': // D
      noteIndex = 62;
      break;
    case 'L': // E
      noteIndex = 64;
      break;
    case 'M': // F
      noteIndex = 65;
      break;
    case 'N': // G
      noteIndex = 67;
      break;
    case 'O': // A
      noteIndex = 69;
      break;
    case 'P': // B
      noteIndex = 71;
      break;
    case 'Q': // C
      noteIndex = 72;
      break;
    case 'R': // D
      noteIndex = 74;
      break;
    case 'S': // E
      noteIndex = 76;
      break;
    case 'T': // F
      noteIndex = 77;
      break;
    case 'U': // G
      noteIndex = 79;
      break;
    case 'V': // A
      noteIndex = 81;
      break;
    case 'W': // B
      noteIndex = 83;
      break;
    case 'X': // C
      noteIndex = 84;
      break;
    case 'Y': // D
      noteIndex = 86;
      break;
    case 'Z': // E
      noteIndex = 88;
      break;
    case '1': // F
      noteIndex = 53;
      break;
    case '2': // G
      noteIndex = 55;
      break;
    case '3': // A
      noteIndex = 57;
      break;
    case '4': // B
      noteIndex = 59;
      break;
    case '5': // C
      noteIndex = 60;
      break;
    case '6': // D
      noteIndex = 62;
      break;
    case '7': // E
      noteIndex = 64;
      break;
    case '8': // F
      noteIndex = 65;
      break;
    case '9': // G
      noteIndex = 67;
      break;
    default:
      noteIndex = 0; // could use these (e.g. F1) for shortcuts
      break;
    }
    return noteIndex;
  }

}
