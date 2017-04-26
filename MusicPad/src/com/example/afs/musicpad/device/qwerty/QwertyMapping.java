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

import com.example.afs.musicpad.device.common.InputMapping;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;

public abstract class QwertyMapping implements InputMapping {

  // http://www.fileformat.info/
  protected static final int ENTER = '\u23ce';
  protected static final int BACK_SPACE = '\u2190';
  protected static final int NUM_LOCK = '#';

  protected int octave = Default.OCTAVE;
  protected final String[] keySequence;

  protected QwertyMapping() {
    keySequence = getKeySequence();
  }

  public int getOctave() {
    return octave;
  }

  @Override
  public void setOctave(int octave) {
    this.octave = octave;
  }

  @Override
  public String toLegend(int midiNote) {
    String keyCap;
    int noteIndex = midiNote - octave * Midi.SEMITONES_PER_OCTAVE;
    if (noteIndex >= 0 && noteIndex < keySequence.length) {
      keyCap = keySequence[noteIndex];
    } else {
      keyCap = "?";
    }
    return keyCap;
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
    case '1':
      semitone = 0; // C
      break;
    case '2':
      semitone = 1; // D
      break;
    case '3':
      semitone = 2; // E
      break;
    case KeyEvent.VK_ENTER:
      semitone = 3; // F
      break;
    case '4':
      semitone = 4; // G
      break;
    case '5':
      semitone = 5; // A
      break;
    case '6':
      semitone = 6; // B
      break;
    case '+':
      semitone = 7; // C
      break;
    case '7':
      semitone = 8; // D
      break;
    case '8':
      semitone = 9; // E
      break;
    case '9':
      semitone = 10; // F
      break;
    case '-':
      semitone = 11; // G
      break;
    case KeyEvent.VK_NUM_LOCK:
      semitone = 12; // A
      break;
    case '/':
      semitone = 13; // B
      break;
    case '*':
      semitone = 14; // C
      break;
    case KeyEvent.VK_BACK_SPACE:
      semitone = 15; // D
      break;
    default:
      semitone = 0; // could use these (e.g. F1) for shortcuts
      break;
    }
    return (octave * Midi.SEMITONES_PER_OCTAVE) + semitone;
  }

  protected abstract String[] getKeySequence();

}
