// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;

public class NotePlayer extends Player {

  public NotePlayer(DeviceHandler deviceHandler, Song song) {
    super(deviceHandler, song);
    initializeOctave();
  }

  @Override
  public void play(Action action, int midiNote) {
    playMidiNote(action, midiNote);
  }

  private void initializeOctave() {
    int octave = inputMapping.getDefaultOctave();
    int lowestMidiNote = getLowestMidiNote();
    int lowestOctave = lowestMidiNote / Midi.SEMITONES_PER_OCTAVE;
    if (lowestOctave < octave) {
      octave = lowestOctave;
    }
    inputMapping.setOctave(octave);
  }

}
