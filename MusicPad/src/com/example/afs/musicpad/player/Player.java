// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import com.example.afs.fluidsynth.FluidSynth;
import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Trace;
import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.util.Velocity;

public class Player {

  public enum Action {
    PRESS, RELEASE
  }

  private static final int PLAYER_BASE = Midi.CHANNELS;
  private static final int PLAYER_CHANNELS = Midi.CHANNELS;
  public static final int TOTAL_CHANNELS = PLAYER_BASE + PLAYER_CHANNELS;

  private static final int DEFAULT_VELOCITY = 96;
  private static final int DEFAULT_PERCENT_VELOCITY = 100;

  protected int deviceIndex;
  private int playerChannel;
  private Synthesizer synthesizer;
  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;

  public Player(Synthesizer synthesizer, int deviceIndex) {
    this.synthesizer = synthesizer;
    this.deviceIndex = deviceIndex;
    this.playerChannel = PLAYER_BASE + deviceIndex;
  }

  public void bendPitch(int pitchBend) {
    synthesizer.bendPitch(playerChannel, pitchBend);
  }

  public void changeControl(int control, int value) {
    synthesizer.changeControl(playerChannel, control, value);
  }

  public void play(Action action, int midiNote) {
    playMidiNote(action, midiNote);
  }

  public void selectProgram(int program) {
    if (program == -1) {
      synthesizer.setChannelType(playerChannel, FluidSynth.CHANNEL_TYPE_DRUM);
      synthesizer.changeProgram(playerChannel, 0); // initialize fluid_synth.c channel
    } else {
      synthesizer.setChannelType(playerChannel, FluidSynth.CHANNEL_TYPE_MELODIC);
      synthesizer.changeProgram(playerChannel, program);
    }
  }

  public void setPercentVelocity(int percentVelocity) {
    this.percentVelocity = percentVelocity;
  }

  protected void playMidiChord(Action action, Chord chord) {
    if (action == Action.PRESS && Trace.isTracePlay()) {
      System.out.println("Player.play: chordType=" + chord);
    }
    for (int midiNote : chord.getMidiNotes()) {
      // TODO: Consider arpeggiator options
      synthesizeNote(action, midiNote);
    }
  }

  protected void playMidiNote(Action action, int midiNote) {
    if (action == Action.PRESS && Trace.isTracePlay()) {
      System.out.println("Player.play: midiNote=" + Names.formatNote(midiNote));
    }
    synthesizeNote(action, midiNote);
  }

  private void synthesizeNote(Action action, int midiNote) {
    switch (action) {
    case PRESS:
      synthesizer.pressKey(playerChannel, midiNote, Velocity.scale(DEFAULT_VELOCITY, percentVelocity));
      break;
    case RELEASE:
      synthesizer.releaseKey(playerChannel, midiNote);
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

}
