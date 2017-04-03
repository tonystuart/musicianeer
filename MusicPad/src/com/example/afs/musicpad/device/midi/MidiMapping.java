// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import com.example.afs.musicpad.device.common.InputMapping;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;

public class MidiMapping extends InputMapping {

  private static final int[] KEY_NOTES = new int[] {
      0, // C
      -1, // C#
      1, // D
      -1, // D#
      2, // E
      3, // F
      -1, // F#
      4, // G
      -1, // G#
      5, // A
      -1, // A#
      6, //
  };

  @Override
  public int fromNoteIndex(int noteIndex) {
    return 0;
  }

  @Override
  public int toNoteIndex(int inputCode) {
    return inputCode;
  }

  public int toNoteIndexOld(int inputCode) {
    int noteIndex = -1;
    if (inputCode >= Default.OCTAVE_SEMITONE) {
      int octave = inputCode / Midi.SEMITONES_PER_OCTAVE;
      int deltaOctave = octave - Default.OCTAVE;
      int baseNote = deltaOctave * Midi.NOTES_PER_OCTAVE;
      int noteInKey = KEY_NOTES[inputCode % KEY_NOTES.length];
      if (noteInKey != -1) {
        noteIndex = baseNote + noteInKey;
      }
    }
    return noteIndex;
  }

}
