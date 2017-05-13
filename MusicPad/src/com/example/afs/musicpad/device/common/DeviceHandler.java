// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

import java.util.HashMap;
import java.util.Map;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.device.midi.MidiMapping;
import com.example.afs.musicpad.device.qwerty.AlphaMapping;
import com.example.afs.musicpad.device.qwerty.NumericMapping;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnControlChange;
import com.example.afs.musicpad.message.OnMusic;
import com.example.afs.musicpad.message.OnNoteOff;
import com.example.afs.musicpad.message.OnNoteOn;
import com.example.afs.musicpad.message.OnPitchBend;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.player.Notator;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.player.PlayerFactory;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Value;

public class DeviceHandler extends BrokerTask<Message> {

  private static int nextDeviceIndex;
  private static Map<String, Integer> devices = new HashMap<>();

  private static synchronized int getDeviceIndex(String name) {
    Integer deviceIndex = devices.get(name);
    if (deviceIndex == null) {
      deviceIndex = nextDeviceIndex++;
      devices.put(name, deviceIndex);
    }
    return deviceIndex;
  }

  private Player player;
  private PlayerFactory playerFactory;
  private Song song = Default.SONG;
  private final String name;
  private final int index;
  private int channel;
  private InputMapping inputMapping;
  private Synthesizer synthesizer;

  public DeviceHandler(Broker<Message> messageBroker, Synthesizer synthesizer, String name) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    this.name = name;
    this.index = getDeviceIndex(name);
    this.playerFactory = new PlayerFactory(this);
    delegate(OnNoteOn.class, message -> doNoteOn(message.getMidiNote()));
    delegate(OnNoteOff.class, message -> doNoteOff(message.getMidiNote()));
    delegate(OnControlChange.class, message -> doControlChange(message.getControl(), message.getValue()));
    delegate(OnPitchBend.class, message -> doPitchBend(message.getPitchBend()));
    delegate(OnCommand.class, message -> doCommand(message));
    subscribe(OnSong.class, message -> doSongSelected(message.getSong()));
  }

  @Override
  public Broker<Message> getBroker() {
    return super.getBroker();
  }

  public int getChannel() {
    return channel;
  }

  public int getIndex() {
    return index;
  }

  public InputMapping getInputMapping() {
    return inputMapping;
  }

  public String getName() {
    return name;
  }

  public Synthesizer getSynthesizer() {
    return synthesizer;
  }

  public void setInputMapping(InputMapping inputMapping) {
    this.inputMapping = inputMapping;
    updatePlayer();
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
      setInputMapping(new AlphaMapping());
      break;
    case SET_NUMERIC_MAPPING:
      setInputMapping(new NumericMapping());
      break;
    case SET_MIDI_MAPPING:
      setInputMapping(new MidiMapping());
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
    this.channel = channel;
    updatePlayer();
  }

  private void selectProgram(int program) {
    player.selectProgram(program);
  }

  private void setVelocity(int velocity) {
    player.setPercentVelocity(Range.scaleMidiToPercent(velocity));
  }

  private void updatePlayer() {
    this.player = playerFactory.createPlayer(song);
    Notator notator = new Notator(player, song, channel);
    String music = notator.getMusic();
    getBroker().publish(new OnCommand(Command.SHOW_CHANNEL_STATE, 0));
    getBroker().publish(new OnMusic(index, music));
  }

}
