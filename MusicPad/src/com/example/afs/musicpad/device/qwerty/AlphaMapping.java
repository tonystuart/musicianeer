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
      N + ",", // C
      S + ",", // C#
      N + ".", // D
      S + ".", // D#
      N + "A", // E
      N + "S", // F
      S + "S", // F#
      N + "D", // G
      S + "D", // G#
      N + "F", // A
      S + "F", // A#
      N + "G", // B
      N + "H", // C
      S + "H", // C#
      N + "J", // D
      S + "J", // D#
      N + "K", // E
      N + "L", // F
      S + "L", // F#
      N + "Q", // G
      S + "Q", // G#
      N + "W", // A
      S + "W", // A#
      N + "E", // B
      N + "R", // C
      S + "R", // C#
      N + "T", // D
      S + "T", // D#
      N + "Y", // E
      N + "U", // F
      S + "U", // F#
      N + "I", // G
      S + "I", // G#
      N + "O", // A
      S + "O", // A#
      N + "P", // B
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
    case ',':
      semitone = 12; // C
      break;
    case '.':
      semitone = 14; // D
      break;
    case 'A':
      semitone = 16; // E
      break;
    case 'S':
      semitone = 17; // F
      break;
    case 'D':
      semitone = 19; // G
      break;
    case 'F':
      semitone = 21; // A
      break;
    case 'G':
      semitone = 23; // B
      break;
    case 'H':
      semitone = 24; // C
      break;
    case 'J':
      semitone = 26; // D
      break;
    case 'K':
      semitone = 28; // E
      break;
    case 'L':
      semitone = 29; // F
      break;
    case 'Q':
      semitone = 31; // G
      break;
    case 'W':
      semitone = 33; // A
      break;
    case 'E':
      semitone = 35; // B
      break;
    case 'R':
      semitone = 36; // C
      break;
    case 'T':
      semitone = 38; // D
      break;
    case 'Y':
      semitone = 40; // E
      break;
    case 'U':
      semitone = 41; // F
      break;
    case 'I':
      semitone = 43; // G
      break;
    case 'O':
      semitone = 45; // A
      break;
    case 'P':
      semitone = 47; // B
      break;
    default:
      semitone = -1; // could use these (e.g. F1) for shortcuts
      break;
    }
    return semitone == -1 ? -1 : (octave * Midi.SEMITONES_PER_OCTAVE) + semitone;
  }

}
