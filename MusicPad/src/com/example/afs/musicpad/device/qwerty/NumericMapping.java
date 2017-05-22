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
  public String deltaToInputCode(int distance) {
    return "=?";
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
    case KeyEvent.VK_ENTER:
      semitone = 5; // F
      break;
    case '4':
      semitone = 7; // G
      break;
    case '5':
      semitone = 9; // A
      break;
    case '6':
      semitone = 11; // B
      break;
    case '+':
      semitone = 12; // C
      break;
    case '7':
      semitone = 14; // D
      break;
    case '8':
      semitone = 16; // E
      break;
    case '9':
      semitone = 17; // F
      break;
    case '-':
      semitone = 19; // G
      break;
    case KeyEvent.VK_NUM_LOCK:
      semitone = 21; // A
      break;
    case '/':
      semitone = 23; // B
      break;
    case '*':
      semitone = 24; // C
      break;
    case KeyEvent.VK_BACK_SPACE:
      semitone = 26; // D
      break;
    default:
      semitone = -1; // could use these (e.g. F1) for shortcuts
      break;
    }
    return semitone == -1 ? -1 : (octave * Midi.SEMITONES_PER_OCTAVE) + semitone;
  }

  @Override
  protected String[] getKeySequence() {
    return KEY_SEQUENCE;
  }

}
