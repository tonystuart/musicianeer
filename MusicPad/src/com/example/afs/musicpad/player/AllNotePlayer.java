// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.midi.Midi;

public class AllNotePlayer extends Player {

  public AllNotePlayer(Synthesizer synthesizer, int channel) {
    super(synthesizer, channel);
  }

  @Override
  public void play(Action action, int noteIndex) {
    if (noteIndex >= 0 && noteIndex < Midi.NOTES) {
      playMidiNote(action, noteIndex);
    }
  }

}
