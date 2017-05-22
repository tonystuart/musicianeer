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
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;

public abstract class QwertyMapping implements InputMapping {

  protected static final String N = ""; // normal
  protected static final String S = "\u2191"; // sharp (shift)

  // http://www.fileformat.info/
  protected static final int ENTER = '\u23ce';
  protected static final int BACK_SPACE = '\u2190';
  protected static final int NUM_LOCK = '#';

  protected int octave = Default.OCTAVE;
  private String[] keySequence;
  private char[] lowerRegisters;
  private char[] higherRegisters;

  public QwertyMapping(String[] keySequence, char[] lowerRegisters, char[] higherRegisters) {
    this.keySequence = keySequence;
    this.lowerRegisters = lowerRegisters;
    this.higherRegisters = higherRegisters;
  }

  @Override
  public int getOctave() {
    return octave;
  }

  @Override
  public int inputCodeToDelta(int inputCode) {
    for (int i = 0; i < lowerRegisters.length; i++) {
      if (inputCode == lowerRegisters[i]) {
        return -((i + 1) * keySequence.length);
      }
    }
    for (int i = 0; i < higherRegisters.length; i++) {
      if (inputCode == higherRegisters[i]) {
        return +((i + 1) * keySequence.length);
      }
    }
    return 0;
  }

  @Override
  public void setOctave(int octave) {
    this.octave = octave;
  }

  @Override
  public String toKeyCap(int midiNote) {
    String keyCap;
    int noteIndex = midiNote - octave * Midi.SEMITONES_PER_OCTAVE;
    if (noteIndex < 0) {
      keyCap = null;
      for (int i = 0; i < lowerRegisters.length && keyCap == null; i++) {
        noteIndex += keySequence.length;
        if (noteIndex > 0 && noteIndex < keySequence.length) {
          keyCap = lowerRegisters[i] + "+" + keySequence[noteIndex];
        }
      }
      if (keyCap == null) {
        keyCap = "<?";
      }
    } else if (noteIndex < keySequence.length) {
      keyCap = keySequence[noteIndex];
    } else {
      keyCap = null;
      for (int i = 0; i < higherRegisters.length && keyCap == null; i++) {
        noteIndex -= keySequence.length;
        if (noteIndex < keySequence.length) {
          keyCap = higherRegisters[i] + "+" + keySequence[noteIndex];
        }
      }
      if (keyCap == null) {
        keyCap = ">?";
      }
    }
    return keyCap;
  }

}
