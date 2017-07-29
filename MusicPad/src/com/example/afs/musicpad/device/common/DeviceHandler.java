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
import com.example.afs.musicpad.device.midi.MidiPlayableMap;
import com.example.afs.musicpad.device.qwerty.AlphaPlayableMap;
import com.example.afs.musicpad.device.qwerty.NumericPlayableMap;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelUpdate;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnSampleChannel;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.playable.PlayableMap;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.song.ChannelNotes;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Range;

public class DeviceHandler extends BrokerTask<Message> {

  public static enum InputType {
    ALPHA, NUMERIC, MIDI, DETACH
  }

  public static enum OutputType {
    NORMAL, ARPEGGIO
  }

  private int deviceIndex;
  private int channel;

  private Song song;
  private Player player;
  private String deviceName;
  private InputType inputType;
  private PlayableMap playableMap;
  private Sound[] activeSounds = new Sound[256]; // NB: KeyEvents VK codes, not midiNotes
  private boolean isCommand;

  public DeviceHandler(Broker<Message> broker, Synthesizer synthesizer, String deviceName, int deviceIndex, InputType inputType) {
    super(broker);
    this.deviceName = deviceName;
    this.deviceIndex = deviceIndex;
    this.inputType = inputType;
    this.player = new Player(synthesizer, deviceIndex);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnSong.class, message -> doSong(message));
    subscribe(OnSampleChannel.class, message -> doSampleChannel(message));
    subscribe(OnDeviceCommand.class, message -> doDeviceCommand(message));
  }

  public void detach() {
    getBroker().publish(new OnDeviceCommand(DeviceCommand.INPUT, deviceIndex, InputType.DETACH.ordinal()));
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
    if (inputCode == KeyEvent.VK_NUM_LOCK) {
      if (inputType == InputType.ALPHA) {
        publish(new OnDeviceCommand(DeviceCommand.INPUT, deviceIndex, InputType.NUMERIC.ordinal()));
      } else {
        publish(new OnDeviceCommand(DeviceCommand.INPUT, deviceIndex, InputType.ALPHA.ordinal()));
      }
    } else if (inputCode == '.') {
      isCommand = true;
    } else if (isCommand) {
      switch (inputCode) {
      case 'B':
        publish(new OnCommand(Command.BACKWARD));
        break;
      case 'F':
        publish(new OnCommand(Command.FORWARD));
        break;
      case 'I':
        publish(new OnCommand(Command.INCREASE_MASTER_GAIN, 0));
        break;
      case 'N':
        publish(new OnCommand(Command.NEW_SONG));
        break;
      case 'P':
        publish(new OnCommand(Command.PLAY, ChannelNotes.ALL_CHANNELS));
        break;
      case 'R':
        publish(new OnCommand(Command.REDUCE_MASTER_GAIN, 0));
        break;
      case 'S':
        publish(new OnCommand(Command.STOP, 0));
        break;
      case 'V':
        publish(new OnCommand(Command.VIEW));
        break;
      case '0':
        publish(new OnCommand(Command.SLOWER, 0));
        break;
      case '1':
        publish(new OnCommand(Command.FASTER, 0));
        break;
      case '2':
        publish(new OnCommand(Command.REDUCE_BACKGROUND_VELOCITY, 0));
        break;
      case '3':
        publish(new OnCommand(Command.INCREASE_BACKGROUND_VELOCITY, 0));
        break;
      case '4':
        publish(new OnDeviceCommand(DeviceCommand.DECREASE_PLAYER_VELOCITY, deviceIndex, 0));
        break;
      case '5':
        publish(new OnDeviceCommand(DeviceCommand.INCREASE_PLAYER_VELOCITY, deviceIndex, 0));
        break;
      case '6':
        publish(new OnDeviceCommand(DeviceCommand.PREVIOUS_CHANNEL, deviceIndex, 0));
        break;
      case '7':
        publish(new OnDeviceCommand(DeviceCommand.NEXT_CHANNEL, deviceIndex, 0));
        break;
      case '8':
        publish(new OnDeviceCommand(DeviceCommand.PREVIOUS_PROGRAM, deviceIndex, 0));
        break;
      case '9':
        publish(new OnDeviceCommand(DeviceCommand.NEXT_PROGRAM, deviceIndex, 0));
        break;
      }
    } else if (playableMap != null) {
      Sound sound = playableMap.onDown(inputCode);
      if (sound != null) {
        if (sound != null) {
          player.play(Action.PRESS, sound);
          activeSounds[inputCode] = sound;
        }
      }
    }
  }

  public void onUp(int inputCode) {
    if (inputCode == '.') {
      isCommand = false;
    } else if (isCommand) {
    } else if (playableMap != null) {
      playableMap.onUp(inputCode);
      Sound sound = activeSounds[inputCode];
      if (sound != null) {
        player.play(Action.RELEASE, sound);
        activeSounds[inputCode] = null;
      }
    }
  }

  @Override
  public String toString() {
    return "DeviceHandler [deviceName=" + deviceName + ", deviceIndex=" + deviceIndex + ", channel=" + channel + ", inputType=" + inputType + "]";
  }

  private PlayableMap createPlayableMap() {
    PlayableMap playableMap;
    switch (inputType) {
    case ALPHA:
      playableMap = new AlphaPlayableMap(song.getChannelNotes(channel), player.getOutputType());
      break;
    case NUMERIC:
      playableMap = new NumericPlayableMap(song.getChannelNotes(channel), player.getOutputType());
      break;
    case MIDI:
      playableMap = new MidiPlayableMap(song.getChannelNotes(channel), player.getOutputType());
      break;
    default:
      throw new UnsupportedOperationException();
    }
    return playableMap;
  }

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    int parameter = message.getParameter();
    switch (command) {
    case PLAY:
      player.setEnabled(true);
      break;
    case STOP:
      player.setEnabled(true);
      break;
    case TEMPO:
      player.setPercentTempo(Range.scaleMidiToPercent(parameter));
      break;
    default:
      break;
    }
  }

  private void doDecreasePlayerVelocity() {
    int currentPlayerVelocity = player.getPercentVelocity();
    int newPlayerVelocity = Math.max(0, currentPlayerVelocity - 5);
    player.setPercentVelocity(newPlayerVelocity);
  }

  private void doDeviceCommand(OnDeviceCommand message) {
    if (this.deviceIndex == message.getDeviceIndex()) {
      DeviceCommand deviceCommand = message.getDeviceCommand();
      int parameter = message.getParameter();
      switch (deviceCommand) {
      case CHANNEL:
        selectChannel(parameter);
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
      case DECREASE_PLAYER_VELOCITY:
        doDecreasePlayerVelocity();
        break;
      case INCREASE_PLAYER_VELOCITY:
        doIncreasePlayerVelocity();
        break;
      case NEXT_CHANNEL:
        doNextChannel();
        break;
      case NEXT_PROGRAM:
        doNextProgram();
        break;
      case PREVIOUS_CHANNEL:
        doPreviousChannel();
        break;
      case PREVIOUS_PROGRAM:
        doPreviousProgram();
        break;
      default:
        break;
      }
    }
  }

  private void doIncreasePlayerVelocity() {
    int currentPlayerVelocity = player.getPercentVelocity();
    int newPlayerVelocity = Math.min(Player.MAX_PERCENT_VELOCITY, currentPlayerVelocity + 5);
    player.setPercentVelocity(newPlayerVelocity);
  }

  private void doInput(int typeIndex) {
    InputType inputType = InputType.values()[typeIndex];
    switch (inputType) {
    case ALPHA:
    case MIDI:
    case NUMERIC:
      this.inputType = inputType;
      updateChannel();
      break;
    case DETACH:
      getBroker().publish(new OnCommand(Command.DETACH, deviceIndex));
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private void doNextChannel() {
    int activeChannel;
    int nextActiveChannel = -1;
    int[] activeChannels = song.getActiveChannels();
    for (int i = activeChannels.length - 1; i >= 0 && (activeChannel = activeChannels[i]) > channel; i--) {
      nextActiveChannel = activeChannel;
    }
    if (nextActiveChannel != -1) {
      selectChannel(nextActiveChannel);
    }
  }

  private void doNextProgram() {
    if (channel != Midi.DRUM) {
      int currentProgram = player.getProgram();
      int nextProgram = currentProgram + 1;
      if (nextProgram < Midi.PROGRAMS) {
        selectProgram(nextProgram);
      }
    }
  }

  private void doOutput(int typeIndex) {
    OutputType outputType = OutputType.values()[typeIndex];
    player.setOutputType(outputType);
    updateChannel();
  }

  private void doPreviousChannel() {
    int activeChannel;
    int previousActiveChannel = -1;
    int[] activeChannels = song.getActiveChannels();
    for (int i = 0; i < activeChannels.length && (activeChannel = activeChannels[i]) < channel; i++) {
      previousActiveChannel = activeChannel;
    }
    if (previousActiveChannel != -1) {
      selectChannel(previousActiveChannel);
    }
  }

  private void doPreviousProgram() {
    if (channel != Midi.DRUM) {
      int currentProgram = player.getProgram();
      int nextProgram = currentProgram - 1;
      if (nextProgram >= 0) {
        selectProgram(nextProgram);
      }
    }
  }

  private void doSampleChannel(OnSampleChannel message) {
    if (message.getDeviceIndex() == deviceIndex) {
      selectChannel(message.getChannel());
      player.setEnabled(true);
    } else {
      player.setEnabled(false);
    }
  }

  private void doSong(OnSong message) {
    song = message.getSong();
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
    updateChannel();
  }

  private void selectProgram(int program) {
    player.selectProgram(program);
  }

  private void setPercentVelocity(int percentVelocity) {
    player.setPercentVelocity(percentVelocity);
  }

  private void updateChannel() {
    playableMap = createPlayableMap();
    getBroker().publish(new OnChannelUpdate(deviceIndex, deviceName, channel, inputType, player.getOutputType(), playableMap));
  }

}
