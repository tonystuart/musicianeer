// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Set;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.device.qwerty.KeyCapMap;
import com.example.afs.musicpad.device.qwerty.MidiKeyCapMap;
import com.example.afs.musicpad.device.qwerty.QwertyKeyCapMap;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelAssigned;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnMusic;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.Chord;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.renderer.ChannelRenderer;
import com.example.afs.musicpad.renderer.Notator;
import com.example.afs.musicpad.renderer.Notator.KeyCap;
import com.example.afs.musicpad.renderer.Notator.Slice;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Value;

public class DeviceHandler extends BrokerTask<Message> {

  public static enum InputType {
    ALPHA, NUMERIC, MIDI, DETACH
  }

  public static enum OutputType {
    NOTE, CHORD, AUTO
  }

  private Song song;
  private int channel;
  private int ticksPerPixel;
  private Player player;
  private InputType inputType;
  private KeyCapMap keyCapMap;
  private Chord[] activeChords = new Chord[256]; // NB: KeyEvents VK codes, not midiNotes
  private int deviceIndex;
  private String deviceName;

  public DeviceHandler(Broker<Message> broker, Synthesizer synthesizer, String deviceName, int deviceIndex, InputType inputType) {
    super(broker);
    this.deviceName = deviceName;
    this.deviceIndex = deviceIndex;
    this.inputType = inputType;
    this.player = new Player(synthesizer, deviceIndex);
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

  public Player getPlayer() {
    return player;
  }

  public void onDown(int inputCode) {
    Chord chord = keyCapMap.onDown(inputCode);
    if (chord != null) {
      if (chord != null) {
        player.play(Action.PRESS, chord);
        activeChords[inputCode] = chord;
      }
    }
  }

  public void onUp(int inputCode) {
    keyCapMap.onUp(inputCode);
    Chord chord = activeChords[inputCode];
    if (chord != null) {
      player.play(Action.RELEASE, chord);
      activeChords[inputCode] = null;
    }
  }

  public RandomAccessList<KeyCap> toKeyCaps(RandomAccessList<Slice> slices) {
    switch (inputType) {
    case ALPHA:
      keyCapMap = new QwertyKeyCapMap("ABCDEFGHIJKLMNOPQRSTUVWXYZ", " " + (char) KeyEvent.VK_SHIFT, slices);
      break;
    case NUMERIC:
      keyCapMap = new QwertyKeyCapMap("123456789", " /*-+", slices);
      break;
    case MIDI:
      keyCapMap = new MidiKeyCapMap(slices);
      break;
    default:
      throw new UnsupportedOperationException();
    }
    return keyCapMap.getKeyCaps();
  }

  private void doChannelAssigned(OnChannelAssigned message) {
    if (this.deviceIndex == message.getDeviceIndex()) {
      this.song = message.getSong();
      this.channel = message.getChannel();
      this.ticksPerPixel = message.getTicksPerPixel();
      updatePlayer();
    }
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
        setPercentVelocity(Range.scaleMidiToPercent(parameter));
        break;
      default:
        break;
      }
    }
  }

  private void doInput(int typeIndex) {
    InputType inputType = InputType.values()[typeIndex];
    switch (inputType) {
    case ALPHA:
    case MIDI:
    case NUMERIC:
      this.inputType = inputType;
      // TODO: Publish OnRender message
      updatePlayer();
      break;
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

  private void doSong(Song song, Map<Integer, Integer> deviceChannelMap, int ticksPerPixel) {
    this.song = song;
    this.ticksPerPixel = ticksPerPixel;
    if (deviceChannelMap.containsKey(deviceIndex)) {
      this.channel = deviceChannelMap.get(deviceIndex);
      updatePlayer();
    } else {
      // TODO: Resolve race condition between publishing initial song and connecting device
      System.err.println("DeviceHandler.doSongSelected: deviceChannelMap does not contain channel for device " + deviceIndex);
    }
  }

  private String getChannelControls() {
    ChannelRenderer channelRenderer = new ChannelRenderer(deviceName, deviceIndex, song, channel, inputType, getOutputType());
    String channelControls = channelRenderer.render();
    return channelControls;
  }

  private String getMusic() {
    Notator notator = new Notator(song, channel, ticksPerPixel, this);
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
    player.selectProgram(program);
  }

  private void setPercentVelocity(int percentVelocity) {
    player.setPercentVelocity(percentVelocity);
  }

  private void updatePlayer() {
    if (song != null) {
      if (channel == Midi.DRUM) {
        selectProgram(-1);
      } else {
        Set<Integer> programs = song.getPrograms(channel);
        if (programs.size() > 0) {
          int program = programs.iterator().next();
          selectProgram(program);
        }
      }
      getBroker().publish(new OnMusic(deviceIndex, getChannelControls(), getMusic()));
    }
  }

}
