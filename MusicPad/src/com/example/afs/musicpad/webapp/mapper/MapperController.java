// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.mapper;

import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;

import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.device.midi.MidiConfiguration;
import com.example.afs.musicpad.device.midi.MidiConfiguration.GroupLabelledIndex;
import com.example.afs.musicpad.device.midi.MidiConfiguration.SoundLabelledIndex;
import com.example.afs.musicpad.device.midi.MidiController;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.message.OnShadowUpdate.Action;
import com.example.afs.musicpad.message.OnShortMessage;
import com.example.afs.musicpad.service.DeviceControllerService;
import com.example.afs.musicpad.service.Services;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.util.JsonUtilities;
import com.example.afs.musicpad.webapp.mapper.MapperView.Mapping;

public class MapperController extends ControllerTask {

  private int deviceIndex;
  private MapperView mapperView;
  private ShortMessage shortMessage;
  private NavigableMap<Integer, Controller> deviceControllers = new TreeMap<>();

  public MapperController(MessageBroker broker) {
    super(broker);
    mapperView = new MapperView(this);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnDeviceCommand.class, message -> doDeviceCommand(message));
    subscribe(OnShortMessage.class, message -> doShortMessage(message));
  }

  @Override
  protected void doClick(String id) {
    if (id.startsWith("device-")) {
      mapperView.selectElement(id, "selected-device");
      deviceIndex = Integer.parseInt(id.substring("device-".length()));
    }
  }

  @Override
  protected void doLoad() {
    // Defer processing that could send shadow update messages until here
    addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "body", mapperView.render()));
    NavigableSet<Integer> devices = request(Services.getDeviceIndexes);
    for (Integer deviceIndex : devices) {
      Controller controller = request(new DeviceControllerService(deviceIndex));
      if (controller instanceof MidiController) {
        deviceControllers.put(deviceIndex, controller);
      }
    }
    mapperView.renderDeviceList(deviceControllers);
    if (deviceControllers.size() > 0) {
      doClick("device-" + deviceControllers.firstKey());
    }
  }

  @Override
  protected void doSubmit(String id, String value) {
    System.out.println("id=" + id + ", value=" + value);
    if (id.equals("command")) {
      Map<String, String> map = JsonUtilities.toMap(value);
      configureCommand(map.get("output"));
    } else if (id.equals("group")) {
      Map<String, String> map = JsonUtilities.toMap(value);
      getConfiguration().put(shortMessage, new GroupLabelledIndex(map.get("group-label"), Integer.parseInt(map.get("group-index"))));
    } else if (id.equals("sound")) {
      Map<String, String> map = JsonUtilities.toMap(value);
      getConfiguration().put(shortMessage, new SoundLabelledIndex(map.get("sound-label"), Integer.parseInt(map.get("sound-index"))));
    }
  }

  private void configureCommand(String value) {
    MidiConfiguration configuration = getConfiguration();
    Mapping mapping = Mapping.valueOf(value);
    switch (mapping) {
    case BACKGROUND_DECREASE_VELOCITY:
      configuration.put(shortMessage, Command.DECREASE_BACKGROUND_VELOCITY);
      break;
    case BACKGROUND_INCREASE_VELOCITY:
      configuration.put(shortMessage, Command.INCREASE_BACKGROUND_VELOCITY);
      break;
    case BACKGROUND_MUTE:
      configuration.put(shortMessage, DeviceCommand.MUTE_BACKGROUND);
      break;
    case BACKGROUND_SELECT_VELOCITY:
      configuration.put(shortMessage, Command.SET_BACKGROUND_VELOCITY);
      break;
    case KARAOKE_TYPE_MEASURE:
      configuration.put(shortMessage, DeviceCommand.OUTPUT_MEASURE);
      break;
    case KARAOKE_TYPE_TICK:
      configuration.put(shortMessage, DeviceCommand.OUTPUT_TICK);
      break;
    case LIBRARY_NEXT_SONG:
      configuration.put(shortMessage, Command.INCREASE_SONG_INDEX);
      break;
    case LIBRARY_PREVIOUS_SONG:
      configuration.put(shortMessage, Command.DECREASE_SONG_INDEX);
      break;
    case LIBRARY_SELECT_SONG:
      configuration.put(shortMessage, Command.SET_SONG_INDEX);
      break;
    case LIBRARY_SELECT_TRANSPOSE:
      configuration.put(shortMessage, Command.SET_TRANSPOSITION);
      break;
    case LIBRARY_TRANSPOSE_HIGHER:
      configuration.put(shortMessage, Command.INCREASE_TRANSPOSITION);
      break;
    case LIBRARY_TRANSPOSE_LOWER:
      configuration.put(shortMessage, Command.DECREASE_TRANSPOSITION);
      break;
    case MASTER_DECREASE_VOLUME:
      configuration.put(shortMessage, Command.DECREASE_MASTER_GAIN);
      break;
    case MASTER_INCREASE_VOLUME:
      configuration.put(shortMessage, Command.INCREASE_MASTER_GAIN);
      break;
    case MASTER_INSTRUMENT:
      configuration.put(shortMessage, Command.SET_MASTER_PROGRAM);
      break;
    case MASTER_SELECT_VOLUME:
      configuration.put(shortMessage, Command.SET_MASTER_GAIN);
      break;
    case PLAYER_DECREASE_VELOCITY:
      configuration.put(shortMessage, DeviceCommand.DECREASE_PLAYER_VELOCITY);
      break;
    case PLAYER_INCREASE_VELOCITY:
      configuration.put(shortMessage, DeviceCommand.INCREASE_PLAYER_VELOCITY);
      break;
    case PLAYER_NEXT_CHANNEL:
      configuration.put(shortMessage, DeviceCommand.NEXT_CHANNEL);
      break;
    case PLAYER_NEXT_PROGRAM:
      configuration.put(shortMessage, DeviceCommand.NEXT_PROGRAM);
      break;
    case PLAYER_PREVIOUS_CHANNEL:
      configuration.put(shortMessage, DeviceCommand.PREVIOUS_CHANNEL);
      break;
    case PLAYER_PREVIOUS_PROGRAM:
      configuration.put(shortMessage, DeviceCommand.PREVIOUS_PROGRAM);
      break;
    case PLAYER_SELECT_CHANNEL:
      configuration.put(shortMessage, DeviceCommand.SELECT_CHANNEL);
      break;
    case PLAYER_SELECT_PROGRAM:
      configuration.put(shortMessage, DeviceCommand.PROGRAM);
      break;
    case PLAYER_SELECT_VELOCITY:
      configuration.put(shortMessage, DeviceCommand.VELOCITY);
      break;
    case TRANSPORT_DECREASE_TEMPO:
      configuration.put(shortMessage, Command.DECREASE_TEMPO);
      break;
    case TRANSPORT_INCREASE_TEMPO:
      configuration.put(shortMessage, Command.INCREASE_TEMPO);
      break;
    case TRANSPORT_NEXT_MEASURE:
      configuration.put(shortMessage, Command.MOVE_FORWARD);
      break;
    case TRANSPORT_PLAY:
      configuration.put(shortMessage, Command.PLAY);
      break;
    case TRANSPORT_PREVIOUS_MEASURE:
      configuration.put(shortMessage, Command.MOVE_BACKWARD);
      break;
    case TRANSPORT_SELECT_MEASURE:
      configuration.put(shortMessage, Command.SEEK);
      break;
    case TRANSPORT_SELECT_TEMPO:
      configuration.put(shortMessage, Command.SET_TEMPO);
      break;
    case TRANSPORT_STOP:
      configuration.put(shortMessage, Command.STOP);
      break;
    default:
      throw new UnsupportedOperationException(mapping.name());
    }
  }

  private void doCommand(OnCommand message) {
  }

  private void doDeviceCommand(OnDeviceCommand message) {
  }

  private void doShortMessage(OnShortMessage message) {
    if (message.getDeviceIndex() == deviceIndex) {
      shortMessage = message.getShortMessage();
      int command = shortMessage.getCommand();
      int channel = shortMessage.getChannel();
      int data1 = shortMessage.getData1();
      int data2 = shortMessage.getData2();
      switch (command) {
      case ShortMessage.NOTE_OFF:
        break;
      case ShortMessage.NOTE_ON:
        mapperView.renderMessageDetails("NOTE_ON", channel, data1, data2);
        break;
      case ShortMessage.POLY_PRESSURE:
        break;
      case ShortMessage.CONTROL_CHANGE:
        mapperView.renderMessageDetails("CONTROL_CHANGE", channel, data1, data2);
        break;
      case ShortMessage.PROGRAM_CHANGE:
        mapperView.renderMessageDetails("PROGRAM_CHANGE", channel, data1, data2);
        break;
      case ShortMessage.CHANNEL_PRESSURE:
        break;
      case ShortMessage.PITCH_BEND:
        mapperView.renderMessageDetails("PITCH_BEND", channel, data1, data2);
        break;
      default:
        mapperView.renderMessageDetails("COMMAND_" + command, channel, data1, data2);
        break;
      }
    }
  }

  private MidiConfiguration getConfiguration() {
    Controller controller = deviceControllers.get(deviceIndex);
    MidiConfiguration configuration = (MidiConfiguration) controller.getConfiguration();
    return configuration;
  }

}
