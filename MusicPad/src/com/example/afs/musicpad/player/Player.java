// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

import com.example.afs.fluidsynth.FluidSynth;
import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Trace;
import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.device.common.Device;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.PrompterData.BrowserWords;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.theory.ChordType;
import com.example.afs.musicpad.util.Velocity;

public abstract class Player {

  public enum Action {
    PRESS, RELEASE
  }

  private static final int PLAYER_BASE = Midi.CHANNELS;
  private static final int PLAYER_CHANNELS = Midi.CHANNELS;
  public static final int TOTAL_CHANNELS = PLAYER_BASE + PLAYER_CHANNELS;

  private static final int DEFAULT_VELOCITY = 96;
  private static final int DEFAULT_PERCENT_VELOCITY = 100;

  protected Song song;
  protected Device device;
  private int deviceChannel;
  private Synthesizer synthesizer;
  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;

  public Player(Synthesizer synthesizer, Song song, Device device) {
    this.synthesizer = synthesizer;
    this.song = song;
    this.device = device;
    initializeDeviceChannel();
    initializeChannelProgram();
  }

  public void bendPitch(int pitchBend) {
    synthesizer.bendPitch(deviceChannel, pitchBend);
  }

  public void changeControl(int control, int value) {
    synthesizer.changeControl(deviceChannel, control, value);
  }

  public abstract PrompterData getPrompterData();

  public abstract void play(Action action, int midiNote);

  public void selectProgram(int program) {
    synthesizer.changeProgram(deviceChannel, program);
  }

  public void setPercentVelocity(int percentVelocity) {
    this.percentVelocity = percentVelocity;
  }

  protected String[] getLegend(int lowest, int highest) {
    int count = (highest - lowest) + 1;
    String[] names = new String[count];
    for (int midiNote = lowest; midiNote <= highest; midiNote++) {
      names[midiNote - lowest] = device.getInputMapping().toLegend(midiNote);
    }
    System.out.println("names=" + Arrays.toString(names));
    return names;
  }

  protected LinkedList<BrowserWords> getWords() {
    LinkedList<BrowserWords> words = new LinkedList<>();
    for (Word word : song.getWords()) {
      BrowserWords browserWords = new BrowserWords(word.getTick(), word.getText());
      words.add(browserWords);
    }
    return words;
  }

  protected boolean isEmptySong() {
    return song == null || song.getNotes().size() == 0;
  }

  protected void playMidiChord(Action action, int baseMidiNote, ChordType chordType) {
    if (action == Action.PRESS && Trace.isTracePlay()) {
      System.out.println("Player.play: chordType=" + chordType);
    }
    for (int midiNote : chordType.getMidiNotes()) {
      try {
        synthesizeNote(action, baseMidiNote + midiNote);
        Thread.sleep(0);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  protected void playMidiNote(Action action, int midiNote) {
    if (action == Action.PRESS && Trace.isTracePlay()) {
      System.out.println("Player.play: midiNote=" + Names.formatNote(midiNote));
    }
    synthesizeNote(action, midiNote);
  }

  private void initializeChannelProgram() {
    Set<Integer> programs = song.getPrograms(device.getChannel());
    if (programs.size() > 0) {
      int program = programs.iterator().next();
      synthesizer.changeProgram(deviceChannel, program);
    }
  }

  private void initializeDeviceChannel() {
    this.deviceChannel = PLAYER_BASE + device.getIndex();
    if (device.getChannel() == Midi.DRUM) {
      synthesizer.setChannelType(deviceChannel, FluidSynth.CHANNEL_TYPE_DRUM);
    } else {
      synthesizer.setChannelType(deviceChannel, FluidSynth.CHANNEL_TYPE_MELODIC);
    }
    synthesizer.changeProgram(deviceChannel, 0); // initialize fluid_synth.c channel
  }

  private void synthesizeNote(Action action, int midiNote) {
    switch (action) {
    case PRESS:
      synthesizer.pressKey(deviceChannel, midiNote, Velocity.scale(DEFAULT_VELOCITY, percentVelocity));
      break;
    case RELEASE:
      synthesizer.releaseKey(deviceChannel, midiNote);
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

}
