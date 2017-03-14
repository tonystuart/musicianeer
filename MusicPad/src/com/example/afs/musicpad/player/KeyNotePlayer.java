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
import com.example.afs.musicpad.theory.Key;

public class KeyNotePlayer extends Player {

  private Key key;

  public KeyNotePlayer(Synthesizer synthesizer, Key key, int channel) {
    super(synthesizer, channel);
    this.key = key;
  }

  @Override
  public int getUniqueCount() {
    return key.getMidiNotes().length;
  }

  @Override
  public void play(Action action, int noteIndex) {
    if (noteIndex < key.getMidiNotes().length) {
      int midiNote = key.getMidiNotes()[noteIndex];
      playMidiNote(action, midiNote);
    }
  }

}
