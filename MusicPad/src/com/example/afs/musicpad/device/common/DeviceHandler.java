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
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnInputPress;
import com.example.afs.musicpad.message.OnInputRelease;
import com.example.afs.musicpad.message.OnSongSelected;
import com.example.afs.musicpad.message.OnPrompterData;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.player.Player.MappingType;
import com.example.afs.musicpad.player.Player.UnitType;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Value;

public class DeviceHandler extends BrokerTask<Message> implements Controllable {

  private Device device;
  private Player player;
  private Synthesizer synthesizer;
  private Song song = Default.SONG;

  public DeviceHandler(Broker<Message> messageBroker, Synthesizer synthesizer, Device device) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    this.device = device;
    this.player = new Player(synthesizer, song, device);
    delegate(OnInputPress.class, message -> doInputPress(message.getInputCode()));
    delegate(OnInputRelease.class, message -> doInputRelease(message.getInputCode()));
    delegate(OnCommand.class, message -> doCommand(message));
    subscribe(OnSongSelected.class, message -> doSongSelected(message.getSong()));
  }

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    int parameter = message.getParameter();
    switch (command) {
    case SELECT_CHORDS:
      selectChords(Value.toIndex(parameter));
      break;
    case SELECT_NOTES:
      selectNotes(Value.toIndex(parameter));
      break;
    case SELECT_PROGRAM:
      selectProgram(Value.toIndex(parameter));
      break;
    case SET_PLAYER_VELOCITY:
      setVelocity(parameter);
      break;
    case SET_KEYBOARD_MAPPING:
      setKeyboardMapping(Value.toIndex(parameter));
      break;
    default:
      getBroker().publish(message);
      break;
    }
  }

  private void doInputPress(int inputCode) {
    player.play(Action.PRESS, inputCode);
  }

  private void doInputRelease(int inputCode) {
    player.play(Action.RELEASE, inputCode);
  }

  private void doSongSelected(Song song) {
    this.song = song;
    updatePlayer();
  }

  private void selectChords(int channel) {
    device.setChannel(channel);
    device.setUnitType(UnitType.CHORD);
    updatePlayer();
  }

  private void selectNotes(int channel) {
    device.setChannel(channel);
    device.setUnitType(UnitType.NOTE);
    updatePlayer();
  }

  private void selectProgram(int program) {
    player.selectProgram(program);
  }

  private void setKeyboardMapping(int index) {
    device.setMappingType(MappingType.values()[index]); // TODO: Create generic enum converter, see Trace.
    updatePlayer();
  }

  private void setVelocity(int velocity) {
    player.setPercentVelocity(Range.scaleMidiToPercent(velocity));
  }

  private void updatePlayer() {
    this.player = new Player(synthesizer, song, device);
    getBroker().publish(new OnCommand(Command.SHOW_CHANNEL_INFO, 0));
    getBroker().publish(new OnPrompterData(player.getPrompterData()));
  }

}
