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
import com.example.afs.musicpad.player.ChordPlayer;
import com.example.afs.musicpad.player.NotePlayer;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.renderer.ChannelRenderer;
import com.example.afs.musicpad.renderer.Notator;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Value;

public class DeviceHandler extends BrokerTask<Message> {

  public static enum InputType {
    NUMERIC, DETACH
  }

  public static enum OutputType {
    NOTE, CHORD, AUTO
  }

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

  private final String deviceName;
  private final int deviceIndex;

  private Song song;
  private Player player;
  private int channel;
  private InputMapping inputMapping;
  private Synthesizer synthesizer;
  private int ticksPerPixel;
  private OutputType desiredOutputType;

  public DeviceHandler(Broker<Message> messageBroker, Synthesizer synthesizer, String name) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    this.deviceName = name;
    this.deviceIndex = getDeviceIndex(name);
    this.inputMapping = new NumericMapping();
    delegate(OnNoteOn.class, message -> doNoteOn(message.getMidiNote()));
    delegate(OnNoteOff.class, message -> doNoteOff(message.getMidiNote()));
    delegate(OnControlChange.class, message -> doControlChange(message.getControl(), message.getValue()));
    delegate(OnPitchBend.class, message -> doPitchBend(message.getPitchBend()));
    subscribe(OnDeviceCommand.class, message -> doDeviceCommand(message.getDeviceCommand(), message.getDeviceIndex(), message.getParameter()));
    subscribe(OnSong.class, message -> doSong(message.getSong(), message.getDeviceChannelMap(), message.getTicksPerPixel()));
    subscribe(OnChannelAssigned.class, message -> doChannelAssigned(message));
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

  public String getDeviceName() {
    return deviceName;
  }

  public InputMapping getInputMapping() {
    return inputMapping;
  }

  public Synthesizer getSynthesizer() {
    return synthesizer;
  }

  private Player createPlayer(Song song) {
    OutputType outputType;
    if (desiredOutputType != null) {
      outputType = desiredOutputType;
    } else {
      outputType = OutputType.NOTE;
    }
    switch (outputType) {
    case CHORD:
      player = new ChordPlayer(this, song);
      break;
    case NOTE:
      player = new NotePlayer(this, song);
      break;
    default:
      break;
    }
    return player;
  }

  private void doChannelAssigned(OnChannelAssigned message) {
    if (this.deviceIndex == message.getDeviceIndex()) {
      this.song = message.getSong();
      this.channel = message.getChannel();
      this.ticksPerPixel = message.getTicksPerPixel();
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
      case OUTPUT:
        doOutput(parameter);
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
    InputType inputType = InputType.values()[typeIndex];
    switch (inputType) {
    case DETACH:
      getBroker().publish(new OnCommand(Command.DETACH, deviceIndex));
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

  private void doOutput(int typeIndex) {
    OutputType outputType = OutputType.values()[typeIndex];
    if (outputType == OutputType.AUTO) {
      desiredOutputType = null;
    } else {
      desiredOutputType = outputType;
    }
    updatePlayer();
  }

  private void doPitchBend(int pitchBend) {
    player.bendPitch(pitchBend);
  }

  private void doSong(Song song, Map<Integer, Integer> deviceChannelMap, int ticksPerPixel) {
    this.song = song;
    this.ticksPerPixel = ticksPerPixel;
    // TODO: Resolve race condition between publishing initial song and connecting device
    if (deviceChannelMap.containsKey(deviceIndex)) {
      this.channel = deviceChannelMap.get(deviceIndex);
      updatePlayer();
    } else {
      System.err.println("DeviceHandler.doSongSelected: deviceChannelMap does not contain channel for device " + deviceIndex);
    }
  }

  private String getChannelControls() {
    ChannelRenderer channelRenderer = new ChannelRenderer(deviceName, deviceIndex, song, channel, getOutputType());
    String channelControls = channelRenderer.render();
    return channelControls;
  }

  private String getMusic() {
    Notator notator = new Notator(player, song, channel, ticksPerPixel);
    String music = notator.getMusic();
    return music;
  }

  private OutputType getOutputType() {
    OutputType outputType;
    if (player instanceof NotePlayer) {
      outputType = OutputType.NOTE;
    } else if (player instanceof ChordPlayer) {
      outputType = OutputType.CHORD;
    } else {
      throw new UnsupportedOperationException();
    }
    return outputType;
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
      this.player = createPlayer(song);
      getBroker().publish(new OnMusic(deviceIndex, getChannelControls(), getMusic()));
    }
  }
}
