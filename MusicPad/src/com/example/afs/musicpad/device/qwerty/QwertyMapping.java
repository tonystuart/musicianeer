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
  protected final String[] keySequence;

  protected QwertyMapping() {
    keySequence = getKeySequence();
  }

  @Override
  public int getOctave() {
    return octave;
  }

  @Override
  public void setOctave(int octave) {
    this.octave = octave;
  }

  @Override
  public String toKeyCap(int midiNote) {
    String keyCap;
    int noteIndex = midiNote - octave * Midi.SEMITONES_PER_OCTAVE;
    if (noteIndex >= 0 && noteIndex < keySequence.length) {
      keyCap = keySequence[noteIndex];
    } else {
      keyCap = "?";
    }
    return keyCap;
  }

  protected abstract String[] getKeySequence();

}
