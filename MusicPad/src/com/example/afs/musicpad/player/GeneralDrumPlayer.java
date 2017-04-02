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
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;

public class GeneralDrumPlayer extends Player {

  public GeneralDrumPlayer(Synthesizer synthesizer, int kitIndex) {
    super(synthesizer, Midi.DRUM);
  }

  @Override
  public int getUniqueCount() {
    return Instruments.getDrumCount();
  }

  @Override
  public void play(Action action, int noteIndex) {
    int midiDrum = Midi.DRUM_BASE + noteIndex;
    playMidiDrum(action, midiDrum);
  }

}
