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
import com.example.afs.musicpad.theory.Key;
import com.example.afs.musicpad.theory.ScaleBasedChordType;
import com.example.afs.musicpad.theory.ScaleBasedChordTypes;

public class KeyChordPlayer extends Player {

  private Key key;
  private ScaleBasedChordTypes scaleBasedChordTypes;

  public KeyChordPlayer(Synthesizer synthesizer, Key key, int channel) {
    super(synthesizer, channel);
    this.key = key;
    this.scaleBasedChordTypes = new ScaleBasedChordTypes(key);
  }

  @Override
  public int getUniqueCount() {
    return key.getMidiNotes().length;
  }

  @Override
  public void play(Action action, int chordIndex) {
    int degree = chordIndex % Midi.NOTES_PER_OCTAVE;
    int octaveAdjustment = (chordIndex / Midi.NOTES_PER_OCTAVE) * Midi.SEMITONES_PER_OCTAVE;
    ScaleBasedChordType scaleBasedChordType = scaleBasedChordTypes.get(degree);
    playMidiChord(action, octaveAdjustment, scaleBasedChordType);
  }

}
