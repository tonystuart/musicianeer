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
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.device.common.InputMapping.MappingType;
import com.example.afs.musicpad.device.midi.MidiMapping;
import com.example.afs.musicpad.device.qwerty.AlphaMapping;
import com.example.afs.musicpad.device.qwerty.NumericMapping;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelAssigned;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnControlChange;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnMusic;
import com.example.afs.musicpad.message.OnNoteOff;
import com.example.afs.musicpad.message.OnNoteOn;
import com.example.afs.musicpad.message.OnPitchBend;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.player.PlayerFactory;
import com.example.afs.musicpad.renderer.ChannelControlsRenderer;
import com.example.afs.musicpad.renderer.Notator;
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

  private final String name;
  private final int deviceIndex;

  private Song song;
  private Player player;
  private int channel;
  private InputMapping inputMapping;
  private Synthesizer synthesizer;
  private PlayerFactory playerFactory;

  public DeviceHandler(Broker<Message> messageBroker, Synthesizer synthesizer, String name) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    this.name = name;
    this.deviceIndex = getDeviceIndex(name);
    this.playerFactory = new PlayerFactory(this);
    delegate(OnNoteOn.class, message -> doNoteOn(message.getMidiNote()));
    delegate(OnNoteOff.class, message -> doNoteOff(message.getMidiNote()));
    delegate(OnControlChange.class, message -> doControlChange(message.getControl(), message.getValue()));
    delegate(OnPitchBend.class, message -> doPitchBend(message.getPitchBend()));
    subscribe(OnDeviceCommand.class, message -> doDeviceCommand(message.getDeviceCommand(), message.getDeviceIndex(), message.getParameter()));
    subscribe(OnSong.class, message -> doSongSelected(message.getSong(), message.getDeviceChannelMap()));
    subscribe(OnChannelAssigned.class, message -> doChannelAssigned(message.getSong(), message.getDeviceIndex(), message.getChannel()));
  }

  @Override
  public Broker<Message> getBroker() {
    return super.getBroker();
  }

  public int getChannel() {
    return channel;
  }

  public int getDeviceIndex() {
    return deviceIndex;
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

  private void doChannelAssigned(Song song, int deviceIndex, int channel) {
    if (this.deviceIndex == deviceIndex) {
      this.song = song;
      this.channel = channel;
      updatePlayer();
    }
  }

  private void doControlChange(int control, int value) {
    player.changeControl(control, value);
  }

  private void doDeviceCommand(DeviceCommand command, int deviceIndex, int parameter) {
    if (deviceIndex == this.deviceIndex) {
      switch (command) {
      case CHANNEL:
        selectChannel(Value.toIndex(parameter));
        break;
      case PROGRAM:
        selectProgram(parameter);
        break;
      case INPUT:
        doInput(parameter);
        break;
      case VELOCITY:
        setVelocity(parameter);
        break;
      default:
        break;
      }
    }
  }

  private void doInput(int typeIndex) {
    MappingType mappingType = MappingType.values()[typeIndex];
    switch (mappingType) {
    case ALPHA:
      setInputMapping(new AlphaMapping());
      break;
    case MIDI:
      setInputMapping(new MidiMapping());
      break;
    case NONE:
      getBroker().publish(new OnCommand(Command.DETACH, deviceIndex));
      break;
    case NUMERIC:
      setInputMapping(new NumericMapping());
      break;
    default:
      throw new UnsupportedOperationException();
    }
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

  private void doSongSelected(Song song, Map<Integer, Integer> deviceChannelMap) {
    this.song = song;
    this.channel = deviceChannelMap.get(deviceIndex);
    updatePlayer();
  }

  private String getChannelControls() {
    ChannelControlsRenderer channelControlsRenderer = new ChannelControlsRenderer(song, channel, inputMapping, deviceIndex);
    String channelControls = channelControlsRenderer.render();
    return channelControls;
  }

  private String getMusic() {
    Notator notator = new Notator(player, song, channel);
    String music = notator.getMusic();
    return music;
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
    if (song != null) {
      this.player = playerFactory.createPlayer(song);
      // TODO: Figure out if this needs to be sent once per device?
      getBroker().publish(new OnCommand(Command.SHOW_CHANNEL_STATE, 0));
      getBroker().publish(new OnMusic(deviceIndex, getChannelControls(), getMusic()));
    }
  }

}
