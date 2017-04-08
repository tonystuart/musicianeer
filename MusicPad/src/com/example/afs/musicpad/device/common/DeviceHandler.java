// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.device.common.ControllableGroup.Controllable;
import com.example.afs.musicpad.device.midi.MidiConfiguration.ChannelState;
import com.example.afs.musicpad.device.midi.MidiMapping;
import com.example.afs.musicpad.device.qwerty.AlphaMapping;
import com.example.afs.musicpad.device.qwerty.NumericMapping;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelState;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnInputPress;
import com.example.afs.musicpad.message.OnInputRelease;
import com.example.afs.musicpad.message.OnSongSelected;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.GeneralDrumPlayer;
import com.example.afs.musicpad.player.KeyChordPlayer;
import com.example.afs.musicpad.player.KeyNotePlayer;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.player.SongChordPlayer;
import com.example.afs.musicpad.player.SongDrumPlayer;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.theory.Keys;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Value;

public abstract class DeviceHandler extends BrokerTask<Message> implements Controllable {

  private Player player;
  protected Song currentSong;
  protected Synthesizer synthesizer;
  protected InputMapping inputMapping;

  public DeviceHandler(Broker<Message> messageBroker, Synthesizer synthesizer, InputMapping inputMapping) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    this.inputMapping = inputMapping;
    this.player = createDefaultPlayer();
    delegate(OnInputPress.class, message -> doInputPress(message.getInputCode()));
    delegate(OnInputRelease.class, message -> doInputRelease(message.getInputCode()));
    delegate(OnCommand.class, message -> doCommand(message));
    subscribe(OnSongSelected.class, message -> doSongSelected(message.getSong()));
    subscribe(OnTick.class, message -> doTick(message.getTick()));
  }

  protected abstract Player createDefaultPlayer();

  protected abstract Player createSongNotePlayer(int channel);

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    int parameter = message.getParameter();
    switch (command) {
    case SELECT_CHORDS:
      selectChords(parameter);
      break;
    case SELECT_NOTES:
      selectNotes(parameter);
      break;
    case SELECT_PROGRAM:
      selectProgram(parameter);
      break;
    case SET_PLAYER_VELOCITY:
      setPercentVelocity(parameter);
      break;
    case SET_KEYBOARD_MAPPING:
      setKeyboardMapping(parameter);
      break;
    default:
      getBroker().publish(message);
      break;
    }
  }

  private void doInputPress(int inputCode) {
    int noteIndex = inputMapping.toNoteIndex(inputCode);
    player.play(Action.PRESS, noteIndex);
  }

  private void doInputRelease(int inputCode) {
    int noteIndex = inputMapping.toNoteIndex(inputCode);
    player.play(Action.RELEASE, noteIndex);
  }

  private void doSongSelected(Song song) {
    player.close();
    currentSong = song;
    player = createDefaultPlayer();
  }

  private void doTick(long tick) {
    player.onTick(tick);
  }

  private void selectChords(int channelNumber) {
    player.close();
    int channel = Value.toIndex(channelNumber);
    if (channel == Midi.DRUM) {
      if (currentSong == null) {
        player = new GeneralDrumPlayer(synthesizer);
      } else {
        player = new SongDrumPlayer(synthesizer, currentSong, inputMapping);
      }
    } else {
      if (currentSong == null) {
        player = new KeyChordPlayer(synthesizer, Keys.CMajor, 0);
      } else {
        player = new SongChordPlayer(synthesizer, currentSong, channel, inputMapping);
      }
    }
  }

  private void selectNotes(int channelNumber) {
    player.close();
    int channel = Value.toIndex(channelNumber);
    if (channel == Midi.DRUM) {
      if (currentSong == null) {
        player = new GeneralDrumPlayer(synthesizer);
      } else {
        player = new SongDrumPlayer(synthesizer, currentSong, inputMapping);
      }
    } else {
      if (currentSong == null) {
        player = new KeyNotePlayer(synthesizer, Keys.CMajor, 0);
      } else {
        player = createSongNotePlayer(channel);
        getBroker().publish(new OnChannelState(channel, ChannelState.SELECTED));
      }
    }
  }

  private void selectProgram(int programNumber) {
    int programIndex = programNumber - 1;
    player.selectProgram(programIndex);
  }

  private void setKeyboardMapping(int mapping) {
    switch (mapping) {
    case 1:
      inputMapping = new NumericMapping();
      break;
    case 2:
      inputMapping = new AlphaMapping();
      break;
    case 3:
      inputMapping = new MidiMapping();
      break;
    }
    player.updateInputDevice(inputMapping);
  }

  private void setPercentVelocity(int percentVelocity) {
    player.setPercentVelocity(percentVelocity);
  }

}
