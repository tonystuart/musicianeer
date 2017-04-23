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
import com.example.afs.musicpad.message.OnNoteOff;
import com.example.afs.musicpad.message.OnNoteOn;
import com.example.afs.musicpad.message.OnPrompterData;
import com.example.afs.musicpad.message.OnSongSelected;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
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
    delegate(OnNoteOn.class, message -> doNoteOn(message.getMidiNote()));
    delegate(OnNoteOff.class, message -> doNoteOff(message.getMidiNote()));
    delegate(OnCommand.class, message -> doCommand(message));
    subscribe(OnSongSelected.class, message -> doSongSelected(message.getSong()));
  }

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    int parameter = message.getParameter();
    switch (command) {
    case SELECT_SCALE_CHORDS:
      selectScaleChords(Value.toIndex(parameter));
      break;
    case SELECT_SONG_CHORDS:
      selectSongChords(Value.toIndex(parameter));
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

  private void doNoteOff(int midiNote) {
    player.play(Action.RELEASE, midiNote);
  }

  private void doNoteOn(int midiNote) {
    player.play(Action.PRESS, midiNote);
  }

  private void doSongSelected(Song song) {
    this.song = song;
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

  private void selectScaleChords(int channel) {
    device.setChannel(channel);
    device.setUnitType(UnitType.SCALE_CHORDS);
    updatePlayer();
  }

  private void selectSongChords(int channel) {
    device.setChannel(channel);
    device.setUnitType(UnitType.SONG_CHORDS);
    updatePlayer();
  }

  private void setMapping(InputMapping inputMapping) {
    device.setInputMapping(inputMapping);
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
