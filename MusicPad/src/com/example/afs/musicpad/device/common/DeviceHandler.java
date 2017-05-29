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
import java.util.Set;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.device.qwerty.Device;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelAssigned;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnControlChange;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnMusic;
import com.example.afs.musicpad.message.OnPitchBend;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.player.Player;
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
  private int channel;
  private Device device;
  private Synthesizer synthesizer;
  private int ticksPerPixel;

  public DeviceHandler(Broker<Message> messageBroker, Synthesizer synthesizer, String name) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    this.deviceName = name;
    this.deviceIndex = getDeviceIndex(name);
    this.device = new Device(new Player(synthesizer, deviceIndex));
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

  public Device getDevice() {
    return device;
  }

  public Synthesizer getSynthesizer() {
    return synthesizer;
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
    device.changeControl(control, value);
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

  private void doOutput(int typeIndex) {
    updatePlayer();
  }

  private void doPitchBend(int pitchBend) {
    device.bendPitch(pitchBend);
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
    Notator notator = new Notator(song, channel, ticksPerPixel, device);
    String music = notator.getMusic();
    return music;
  }

  private OutputType getOutputType() {
    return OutputType.NOTE;
  }

  private void selectChannel(int channel) {
    this.channel = channel;
    updatePlayer();
  }

  private void selectProgram(int program) {
    device.selectProgram(program);
  }

  private void setVelocity(int velocity) {
    device.setPercentVelocity(Range.scaleMidiToPercent(velocity));
  }

  private void updatePlayer() {
    if (song != null) {
      Set<Integer> programs = song.getPrograms(channel);
      if (programs.size() > 0) {
        int program = programs.iterator().next();
        device.selectProgram(program);
      }
      getBroker().publish(new OnMusic(deviceIndex, getChannelControls(), getMusic()));
    }
  }

}
