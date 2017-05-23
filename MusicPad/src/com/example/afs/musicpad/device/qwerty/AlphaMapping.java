// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import java.awt.event.KeyEvent;

import com.example.afs.musicpad.midi.Midi;

public class AlphaMapping extends QwertyMapping {

  private static final String[] keySequence = new String[] {
      N + "Z", // C
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
      N + "A", // C
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
      N + "K", // C
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
      N + "Y", // C
      S + "Y", // C#
      N + "U", // D
      S + "U", // D#
      N + "I", // E
      N + "O", // F
      S + "O", // F#
      N + "P", // G
      S + "P", // G#
      N + "[", // A
      S + "[", // A#
      N + "]", // B
  };

  private static final char[] lowerRegisters = new char[] {
      '5',
      '4',
      '3',
      '2',
      '1'
  };

  private static final char[] higherRegisters = new char[] {
      '6',
      '7',
      '8',
      '9',
      '0'
  };

  public AlphaMapping() {
    super(keySequence, lowerRegisters, higherRegisters);
  }

  @Override
  public int getDefaultOctave() {
    return 3;
  }

  @Override
  public int getDefaultRange() {
    return 44; // from C3 to G#6
  }

  @Override
  public char getSharp() {
    return KeyEvent.VK_SHIFT;
  }

  @Override
  public int toMidiNote(int inputCode) {
    int semitone;
    switch (inputCode) {
    case 'Z':
      semitone = 0; // C
      break;
    case 'X':
      semitone = 2; // D
      break;
    case 'C':
      semitone = 4; // E
      break;
    case 'V':
      semitone = 5; // F
      break;
    case 'B':
      semitone = 7; // G
      break;
    case 'N':
      semitone = 9; // A
      break;
    case 'M':
      semitone = 11; // B
      break;
    case 'A':
      semitone = 12; // C
      break;
    case 'S':
      semitone = 14; // D
      break;
    case 'D':
      semitone = 16; // E
      break;
    case 'F':
      semitone = 17; // F
      break;
    case 'G':
      semitone = 19; // G
      break;
    case 'H':
      semitone = 21; // A
      break;
    case 'J':
      semitone = 23; // B
      break;
    case 'K':
      semitone = 24; // C
      break;
    case 'L':
      semitone = 26; // D
      break;
    case 'Q':
      semitone = 28; // E
      break;
    case 'W':
      semitone = 29; // F
      break;
    case 'E':
      semitone = 31; // G
      break;
    case 'R':
      semitone = 33; // A
      break;
    case 'T':
      semitone = 35; // B
      break;
    case 'Y':
      semitone = 36; // C
      break;
    case 'U':
      semitone = 38; // D
      break;
    case 'I':
      semitone = 40; // E
      break;
    case 'O':
      semitone = 41; // F
      break;
    case 'P':
      semitone = 43; // G
      break;
    case '[':
      semitone = 45; // A
      break;
    case ']':
      semitone = 47; // B
      break;
    default:
      semitone = -1; // could use these (e.g. F1) for shortcuts
      break;
    }
    return semitone == -1 ? -1 : (octave * Midi.SEMITONES_PER_OCTAVE) + semitone;
  }

}
