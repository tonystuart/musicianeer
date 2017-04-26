// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.device.common.InputMapping;

public class MidiMapping implements InputMapping {

  private int octave;

  @Override
  public int getBaseOctave() {
    return 36;
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
  public String toLegend(int midiNote) {
    return Names.getNoteName(midiNote);
  }

  @Override
  public int toMidiNote(int noteIndex) {
    return noteIndex;
  }

}
