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
import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.theory.ChordType;

public abstract class Player {

  public enum Action {
    PRESS, RELEASE
  }

  private int channel;
  private Synthesizer synthesizer;

  public Player(Synthesizer synthesizer, int channel) {
    this.synthesizer = synthesizer;
    this.channel = channel;
  }

  public void close() {
  }

  public abstract int getUniqueCount();

  public void onTick(long tick) {
  }

  public abstract void play(Action action, int buttonIndex);

  public void selectProgram(int program) {
    synthesizer.changeProgram(channel, program);
  }

  protected void playMidiChord(Action action, int octave, ChordType chordType) {
    if (action == Action.PRESS) {
      System.out.println("Player.play: chordType=" + chordType);
    }
    for (int midiNote : chordType.getMidiNotes()) {
      try {
        synthesizeNote(action, octave + midiNote);
        Thread.sleep(0);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  protected void playMidiDrum(Action action, int midiDrum) {
    if (action == Action.PRESS) {
      System.out.println("Player.play: midiDrum=" + Names.formatDrum(midiDrum));
    }
    synthesizeNote(action, midiDrum);
  }

  protected void playMidiNote(Action action, int midiNote) {
    if (action == Action.PRESS) {
      System.out.println("Player.play: midiNote=" + Names.formatNote(midiNote));
    }
    synthesizeNote(action, midiNote);
  }

  protected void synthesizeNote(Action action, int midiNote) {
    switch (action) {
    case PRESS:
      synthesizer.pressKey(channel, midiNote, 92);
      break;
    case RELEASE:
      synthesizer.releaseKey(channel, midiNote);
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

}
