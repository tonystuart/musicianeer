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
import com.example.afs.musicpad.device.midi.MidiMapping;
import com.example.afs.musicpad.device.qwerty.AlphaMapping;
import com.example.afs.musicpad.device.qwerty.NumericMapping;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnControlChange;
import com.example.afs.musicpad.message.OnNoteOff;
import com.example.afs.musicpad.message.OnNoteOn;
import com.example.afs.musicpad.message.OnPitchBend;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.player.PlayerFactory;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Value;

public class DeviceHandler extends BrokerTask<Message> implements Controllable {

  private Device device;
  private Player player;
  private PlayerFactory playerFactory;
  private Song song = Default.SONG;

  public DeviceHandler(Broker<Message> messageBroker, Synthesizer synthesizer, Device device) {
    super(messageBroker);
    this.device = device;
    this.playerFactory = new PlayerFactory(synthesizer);
    delegate(OnNoteOn.class, message -> doNoteOn(message.getMidiNote()));
    delegate(OnNoteOff.class, message -> doNoteOff(message.getMidiNote()));
    delegate(OnControlChange.class, message -> doControlChange(message.getControl(), message.getValue()));
    delegate(OnPitchBend.class, message -> doPitchBend(message.getPitchBend()));
    delegate(OnCommand.class, message -> doCommand(message));
    subscribe(OnSong.class, message -> doSongSelected(message.getSong()));
  }

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    int parameter = message.getParameter();
    switch (command) {
    case SELECT_CHANNEL:
      selectChannel(Value.toIndex(parameter));
      break;
    case SELECT_PROGRAM:
      selectProgram(Value.toIndex(parameter));
      break;
    case SET_PLAYER_VELOCITY:
      setVelocity(parameter);
      break;
    case SET_ALPHA_MAPPING:
      setMapping(new AlphaMapping());
      break;
    case SET_NUMERIC_MAPPING:
      setMapping(new NumericMapping());
      break;
    case SET_MIDI_MAPPING:
      setMapping(new MidiMapping());
      break;
    default:
      getBroker().publish(message);
      break;
    }
  }

  private void doControlChange(int control, int value) {
    player.changeControl(control, value);
  }

  private void doNoteOff(int midiNote) {
    player.play(Action.RELEASE, midiNote);
  }

  private void doNoteOn(int midiNote) {
    player.play(Action.PRESS, midiNote);
  }

  private void doPitchBend(int pitchBend) {
    player.bendPitch(pitchBend);
  }

  private void doSongSelected(Song song) {
    this.song = song;
    // TODO: Select default channel based on device index and capabilities
    updatePlayer();
  }

  private void selectChannel(int channel) {
    device.setChannel(channel);
    updatePlayer();
  }

  private void selectProgram(int program) {
    player.selectProgram(program);
  }

  private void setMapping(InputMapping inputMapping) {
    device.setInputMapping(inputMapping);
    updatePlayer();
  }

  private void setVelocity(int velocity) {
    player.setPercentVelocity(Range.scaleMidiToPercent(velocity));
  }

  private void updatePlayer() {
    this.player = playerFactory.createPlayer(song, device);
    getBroker().publish(new OnCommand(Command.SHOW_CHANNEL_INFO, 0));
    getBroker().publish(player.getOnSongMusic());
  }

}
