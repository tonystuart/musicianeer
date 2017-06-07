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
import java.util.Set;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.device.midi.MidiKeyCapMap;
import com.example.afs.musicpad.device.qwerty.QwertyKeyCapMap;
import com.example.afs.musicpad.keycap.KeyCapMap;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelAssigned;
import com.example.afs.musicpad.message.OnChannelUpdate;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Value;

public class DeviceHandler extends BrokerTask<Message> {

  public static enum InputType {
    ALPHA, NUMERIC, MIDI, DETACH
  }

  public static enum OutputType {
    NORMAL, ARPEGGIO
  }

  private Song song;
  private int channel;
  private Player player;
  private InputType inputType;
  private KeyCapMap keyCapMap;
  private Sound[] activeSounds = new Sound[256]; // NB: KeyEvents VK codes, not midiNotes
  private int deviceIndex;
  private String deviceName;

  public DeviceHandler(Broker<Message> broker, Synthesizer synthesizer, String deviceName, int deviceIndex, InputType inputType) {
    super(broker);
    this.deviceName = deviceName;
    this.deviceIndex = deviceIndex;
    this.inputType = inputType;
    this.player = new Player(synthesizer, deviceIndex);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnDeviceCommand.class, message -> doDeviceCommand(message));
    subscribe(OnChannelAssigned.class, message -> doChannelAssigned(message));
  }

  @Override
  public Broker<Message> getBroker() {
    return super.getBroker();
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
    Sound sound = keyCapMap.onDown(inputCode);
    if (sound != null) {
      if (sound != null) {
        player.play(Action.PRESS, sound);
        activeSounds[inputCode] = sound;
      }
    }
  }

  public void onUp(int inputCode) {
    keyCapMap.onUp(inputCode);
    Sound sound = activeSounds[inputCode];
    if (sound != null) {
      player.play(Action.RELEASE, sound);
      activeSounds[inputCode] = null;
    }
  }

  private KeyCapMap createKeyCapMap() {
    KeyCapMap keyCapMap;
    switch (inputType) {
    case ALPHA:
      keyCapMap = new QwertyKeyCapMap("ABCDEFGHIJKLMNOPQRSTUVWXYZ", " " + (char) KeyEvent.VK_SHIFT, player.getOutputType());
      break;
    case NUMERIC:
      keyCapMap = new QwertyKeyCapMap("123456789", " 0/*-+", player.getOutputType());
      break;
    case MIDI:
      keyCapMap = new MidiKeyCapMap();
      break;
    default:
      throw new UnsupportedOperationException();
    }
    return keyCapMap;
  }

  private void doChannelAssigned(OnChannelAssigned message) {
    if (this.deviceIndex == message.getDeviceIndex()) {
      this.song = message.getSong();
      selectChannel(message.getChannel());
    }
  }

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    int parameter = message.getParameter();
    switch (command) {
    case TEMPO:
      player.setPercentTempo(Range.scaleMidiToPercent(parameter));
      break;
    default:
      break;
    }
  }

  private void doDeviceCommand(OnDeviceCommand message) {
    if (this.deviceIndex == message.getDeviceIndex()) {
      DeviceCommand deviceCommand = message.getDeviceCommand();
      int parameter = message.getParameter();
      switch (deviceCommand) {
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
      publishChannelUpdate();
      break;
    case DETACH:
      getBroker().publish(new OnCommand(Command.DETACH, deviceIndex));
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private void doOutput(int typeIndex) {
    OutputType outputType = OutputType.values()[typeIndex];
    player.setOutputType(outputType);
    publishChannelUpdate();
  }

  private void publishChannelUpdate() {
    keyCapMap = createKeyCapMap();
    getBroker().publish(new OnChannelUpdate(deviceIndex, deviceName, channel, inputType, player.getOutputType(), keyCapMap));
  }

  private void selectChannel(int channel) {
    this.channel = channel;
    if (channel == Midi.DRUM) {
      selectProgram(-1);
    } else {
      Set<Integer> programs = song.getPrograms(channel);
      if (programs.size() > 0) {
        int program = programs.iterator().next();
        selectProgram(program);
      }
    }
    publishChannelUpdate();
  }

  private void selectProgram(int program) {
    player.selectProgram(program);
  }

  private void setPercentVelocity(int percentVelocity) {
    player.setPercentVelocity(percentVelocity);
  }

}
