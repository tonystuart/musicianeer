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
import com.example.afs.musicpad.CommandProcessor;
import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.device.common.InputMapping;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.theory.ChordType;
import com.example.afs.musicpad.util.Velocity;

public abstract class Player {

  public enum Action {
    PRESS, RELEASE
  }

  public static final int PLAYER_BASE = Midi.CHANNELS;
  public static final int PLAYER_CHANNELS = Midi.CHANNELS;
  public static final int TOTAL_CHANNELS = PLAYER_BASE + PLAYER_CHANNELS;

  public static final int DEFAULT_VELOCITY = 96;
  public static final int DEFAULT_PERCENT_VELOCITY = 100;

  private int channel;
  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;
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
    synthesizer.changeProgram(PLAYER_BASE + channel, program);
  }

  public void setPercentVelocity(int percentVelocity) {
    this.percentVelocity = percentVelocity;
  }

  public void updateInputDevice(InputMapping inputMapping) {
  }

  protected void playMidiChord(Action action, int octave, ChordType chordType) {
    if (action == Action.PRESS && CommandProcessor.isTracePlay()) {
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
    if (action == Action.PRESS && CommandProcessor.isTracePlay()) {
      System.out.println("Player.play: midiDrum=" + Names.formatDrum(midiDrum));
    }
    synthesizeNote(action, midiDrum);
  }

  protected void playMidiNote(Action action, int midiNote) {
    if (action == Action.PRESS && CommandProcessor.isTracePlay()) {
      System.out.println("Player.play: midiNote=" + Names.formatNote(midiNote));
    }
    synthesizeNote(action, midiNote);
  }

  protected void synthesizeNote(Action action, int midiNote) {
    switch (action) {
    case PRESS:
      synthesizer.pressKey(PLAYER_BASE + channel, midiNote, Velocity.scale(DEFAULT_VELOCITY, percentVelocity));
      break;
    case RELEASE:
      synthesizer.releaseKey(PLAYER_BASE + channel, midiNote);
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

}
