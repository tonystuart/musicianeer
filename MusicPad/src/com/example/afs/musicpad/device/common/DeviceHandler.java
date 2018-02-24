// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

import java.util.Set;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnConfigurationChange;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnKeyDown;
import com.example.afs.musicpad.message.OnKeyUp;
import com.example.afs.musicpad.message.OnRenderSong;
import com.example.afs.musicpad.message.OnSampleChannel;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.PlayableMap;
import com.example.afs.musicpad.player.PlayableMap.OutputType;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.player.PlayerDetail;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.service.BackgroundMuteService;
import com.example.afs.musicpad.service.DeviceControllerService;
import com.example.afs.musicpad.service.PlayerDetailService;
import com.example.afs.musicpad.service.PlayerVelocityService;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask;
import com.example.afs.musicpad.util.Range;

public class DeviceHandler extends ServiceTask {

  public static final int DEFAULT_PERCENT_VELOCITY = 50;

  private int channel;
  private int deviceIndex;
  private int velocity = Range.scalePercentToMidi(DEFAULT_PERCENT_VELOCITY);

  private Song song;
  private Player player;
  private Controller controller;
  private Synthesizer synthesizer;
  private PlayableMap playableMap;
  private Sound[] activeSounds = new Sound[256]; // NB: KeyEvents VK codes, not midiNotes

  public DeviceHandler(MessageBroker broker, Synthesizer synthesizer, int deviceIndex) {
    super(broker);
    this.synthesizer = synthesizer;
    this.deviceIndex = deviceIndex;
    this.player = new Player(synthesizer, deviceIndex);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnRenderSong.class, message -> doRenderSong(message));
    subscribe(OnSampleChannel.class, message -> doSampleChannel(message));
    subscribe(OnDeviceCommand.class, message -> doDeviceCommand(message));
    subscribe(OnConfigurationChange.class, message -> doConfigurationChange(message));
    provide(new PlayerDetailService(deviceIndex), () -> getPlayerDetail());
    provide(new PlayerVelocityService(deviceIndex), () -> getPercentVelocity());
    provide(new BackgroundMuteService(deviceIndex), () -> synthesizer.isMuted(channel));
    provide(new DeviceControllerService(deviceIndex), () -> controller);
  }

  public int tsGetDeviceIndex() {
    return deviceIndex;
  }

  public void tsOnChannelPressure(int channel, int pressure) {
    synthesizer.setChannelPressure(channel, pressure);
  }

  public void tsOnControlChangle(int control, int value) {
    player.changeControl(control, value);
  }

  public void tsOnDown(int inputCode) {
    processDown(inputCode, velocity);
  }

  public void tsOnDown(int inputCode, int velocity) {
    int value;
    // If input is not velocity sensitive
    if (velocity == Midi.MAX_VALUE) {
      // Use preset velocity for this device
      value = this.velocity;
    } else {
      // Otherwise use half of preset velocity as floor and scale input velocity above that
      value = Range.scale(this.velocity / 2, Midi.MAX_VALUE, 0, Midi.MAX_VALUE, velocity);
    }
    processDown(inputCode, value);
  }

  public void tsOnNoteOff(int midiNote) {
    player.noteOff(midiNote, 0);
  }

  public void tsOnNoteOn(int midiNote, int velocity) {
    player.noteOn(midiNote, velocity);
  }

  public void tsOnPitchBend(int pitchBend) {
    player.bendPitch(pitchBend);
  }

  public void tsOnUp(int inputCode) {
    processUp(inputCode, velocity);
  }

  public void tsSetController(Controller controller) {
    this.controller = controller;
  }

  private void createPlayableMap() {
    if (song != null) {
      InputMap groupInputMap = controller.getConfiguration().getGroupInputMap();
      InputMap soundInputMap = controller.getConfiguration().getSoundInputMap();
      Iterable<Note> channelNotes = song.getChannelNotes(channel);
      playableMap = new PlayableMap(groupInputMap, soundInputMap, channelNotes, player.getOutputType());
    }
  }

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    int parameter = message.getParameter();
    switch (command) {
    case SET_MASTER_PROGRAM:
      doSetMasterProgram(parameter);
      break;
    case RESET:
      doReset();
      break;
    case SET_TEMPO:
      player.setPercentTempo(parameter);
      break;
    default:
      break;
    }
  }

  private void doConfigurationChange(OnConfigurationChange message) {
    if (message.getDeviceIndex() == deviceIndex) {
      createPlayableMap();
    }
  }

  private void doDecreasePlayerVelocity() {
    publish(new OnDeviceCommand(DeviceCommand.VELOCITY, deviceIndex, Math.max(0, getPercentVelocity() - 5)));
  }

  private void doDeviceCommand(OnDeviceCommand message) {
    if (message.getDeviceIndex() == deviceIndex) {
      DeviceCommand deviceCommand = message.getDeviceCommand();
      int parameter = message.getParameter();
      switch (deviceCommand) {
      case DECREASE_PLAYER_VELOCITY:
        doDecreasePlayerVelocity();
        break;
      case INCREASE_PLAYER_VELOCITY:
        doIncreasePlayerVelocity();
        break;
      case OUTPUT:
        doOutput(parameter);
        break;
      case OUTPUT_MEASURE:
        setOutputType(OutputType.MEASURE);
        break;
      case OUTPUT_TICK:
        setOutputType(OutputType.TICK);
        break;
      case MUTE_BACKGROUND:
        doMuteBackground(parameter);
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
      case PROGRAM:
        doProgram(parameter);
        break;
      case SELECT_CHANNEL:
        selectChannel(parameter);
        break;
      case VELOCITY:
        setPercentVelocity(parameter);
        break;
      default:
        break;
      }
    }
  }

  private void doIncreasePlayerVelocity() {
    publish(new OnDeviceCommand(DeviceCommand.VELOCITY, deviceIndex, Math.min(100, getPercentVelocity() + 5)));
  }

  private void doMuteBackground(int parameter) {
    synthesizer.muteChannel(channel, parameter != 0);
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
      int currentProgram = player.getChannelProgram();
      int nextProgram = currentProgram + 1;
      if (nextProgram == Midi.PROGRAMS) {
        nextProgram = 0;
      }
      player.setChannelProgram(nextProgram);
    }
  }

  private void doOutput(int typeIndex) {
    OutputType outputType = OutputType.values()[typeIndex];
    setOutputType(outputType);
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
      int currentProgram = player.getChannelProgram();
      int nextProgram = currentProgram - 1;
      if (nextProgram < 0) {
        nextProgram = Midi.PROGRAMS - 1;
      }
      player.setChannelProgram(nextProgram);
    }
  }

  private void doProgram(int program) {
    player.setChannelProgram(program);
  }

  private void doRenderSong(OnRenderSong message) {
    player.setEnabled(true);
  }

  private void doReset() {
    publish(new OnDeviceCommand(DeviceCommand.MUTE_BACKGROUND, deviceIndex, 0));
    publish(new OnDeviceCommand(DeviceCommand.VELOCITY, deviceIndex, DEFAULT_PERCENT_VELOCITY));
  }

  private void doSampleChannel(OnSampleChannel message) {
    if (message.getDeviceIndex() == deviceIndex) {
      song = message.getSong();
      selectChannel(message.getChannel());
      player.setEnabled(true);
    } else {
      player.setEnabled(false);
    }
  }

  private void doSetMasterProgram(int masterProgram) {
    player.setMasterProgram(masterProgram);
  }

  private int getPercentVelocity() {
    return Range.scaleMidiToPercent(velocity);
  }

  private PlayerDetail getPlayerDetail() {
    if (playableMap == null) {
      createPlayableMap();
    }
    return new PlayerDetail(playableMap.getPlayables(), channel, player.getChannelProgram());
  }

  private void processDown(int inputCode, int velocity) {
    if (playableMap != null) {
      Sound sound = playableMap.onDown(inputCode);
      if (sound != null) {
        if (sound != null) {
          player.play(Action.PRESS, sound, velocity);
          activeSounds[inputCode] = sound;
          publish(new OnKeyDown(deviceIndex, sound));
        }
      }
    }
  }

  private void processUp(int inputCode, int velocity) {
    if (playableMap != null) {
      playableMap.onUp(inputCode);
      Sound sound = activeSounds[inputCode];
      if (sound != null) {
        player.play(Action.RELEASE, sound, velocity);
        activeSounds[inputCode] = null;
        publish(new OnKeyUp(deviceIndex, sound));
      }
    }
  }

  private void selectChannel(int channel) {
    this.channel = channel;
    if (channel == Midi.DRUM) {
      player.setChannelProgram(Player.DRUM_CHANNEL_PROGRAM);
    } else {
      // TODO: Request current song rather than maintaining state
      Set<Integer> programs = song.getPrograms(channel);
      if (programs.size() > 0) {
        int program = programs.iterator().next();
        player.setChannelProgram(program);
      }
    }
    createPlayableMap();
  }

  private void setOutputType(OutputType outputType) {
    player.setOutputType(outputType);
    createPlayableMap();
  }

  private void setPercentVelocity(int velocity) {
    this.velocity = Range.scalePercentToMidi(velocity);
  }

}
