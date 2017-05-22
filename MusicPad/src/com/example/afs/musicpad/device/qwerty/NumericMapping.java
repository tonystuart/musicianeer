// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import com.example.afs.musicpad.midi.Midi;

public class NumericMapping extends QwertyMapping {

  private static final String[] keySequence = new String[] {
      N + "1", // C
      S + "1", // C#
      N + "2", // D
      S + "2", // D#
      N + "3", // E
      N + "4", // F
      S + "4", // F#
      N + "5", // G
      S + "5", // G#
      N + "6", // A
      S + "6", // A#
      N + "7", // B
      N + "8", // C
      S + "8", // C#
      N + "9", // D
      S + "9", // D#
  };

  private static final char[] lowerRegisters = new char[] {
      '/',
      '*',
  };

  private static final char[] higherRegisters = new char[] {
      '-',
      '+',
  };

  public NumericMapping() {
    super(keySequence, lowerRegisters, higherRegisters);
  }

  @Override
  public int getDefaultOctave() {
    return 4;
  }

  @Override
  public int getDefaultRange() {
    return 27; // From C4 to D#4
  }

  @Override
  public char getSharp() {
    return '0';
  }

  @Override
  public int inputCodeToDelta(int inputCode) {
    return 0;
  }

  @Override
  public int toMidiNote(int inputCode) {
    int semitone;
    switch (inputCode) {
    case '1':
      semitone = 0; // C
      break;
    case '2':
      semitone = 2; // D
      break;
    case '3':
      semitone = 4; // E
      break;
    case '4':
      semitone = 5; // F
      break;
    case '5':
      semitone = 7; // G
      break;
    case '6':
      semitone = 9; // A
      break;
    case '7':
      semitone = 11; // B
      break;
    case '8':
      semitone = 12; // C
      break;
    case '9':
      semitone = 14; // D
      break;
    default:
      semitone = -1; // could use these (e.g. F1) for shortcuts
      break;
    }
    return semitone == -1 ? -1 : (octave * Midi.SEMITONES_PER_OCTAVE) + semitone;
  }

}
