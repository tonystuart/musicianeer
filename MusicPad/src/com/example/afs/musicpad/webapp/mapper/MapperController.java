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
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;

import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.device.midi.InputMessage;
import com.example.afs.musicpad.device.midi.InputType;
import com.example.afs.musicpad.device.midi.MidiConfiguration;
import com.example.afs.musicpad.device.midi.MidiController;
import com.example.afs.musicpad.device.midi.OutputMessage;
import com.example.afs.musicpad.device.midi.OutputType;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceAttached;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnDeviceDetached;
import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.message.OnShadowUpdate.Action;
import com.example.afs.musicpad.message.OnShortMessage;
import com.example.afs.musicpad.service.DeviceControllerService;
import com.example.afs.musicpad.service.Services;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.util.JsonUtilities;

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
    subscribe(OnDeviceAttached.class, message -> doDeviceAttached(message));
    subscribe(OnDeviceDetached.class, message -> doDeviceDetached(message));
  }

  @Override
  protected void doClick(String id) {
    if (id.startsWith("mapping-")) {
      mapperView.selectMapping(id);
    }
  }

  @Override
  protected void doInput(String id, String value) {
    if (id.startsWith("device-type")) {
      displayMappings(Integer.parseInt(value));
    } else if (id.startsWith("input-type-mapping-")) {
      String mappingId = id.substring("input-type-".length());
      Parent parent = mapperView.getElementById(mappingId);
      Mapping mapping = parent.getData();
      OutputMessage outputMessage = mapping.getOutputMessage();
      outputMessage.setInputType(InputType.values()[Integer.parseInt(value)]);
      getConfiguration().put(mapping.getInputMessage(), outputMessage);
    } else if (id.startsWith("output-type-mapping-")) {
      String mappingId = id.substring("output-type-".length());
      Parent parent = mapperView.getElementById(mappingId);
      Mapping mapping = parent.getData();
      OutputMessage outputMessage = mapping.getOutputMessage();
      outputMessage.setOutputType(OutputType.values()[Integer.parseInt(value)]);
      getConfiguration().put(mapping.getInputMessage(), outputMessage);
    } else if (id.startsWith("index-mapping-")) {
      String mappingId = id.substring("index-type-".length());
      Parent parent = mapperView.getElementById(mappingId);
      Mapping mapping = parent.getData();
      OutputMessage outputMessage = mapping.getOutputMessage();
      outputMessage.setIndex(Integer.parseInt(value));
      getConfiguration().put(mapping.getInputMessage(), outputMessage);
    } else if (id.startsWith("label-mapping-")) {
      String mappingId = id.substring("label-type-".length());
      Parent parent = mapperView.getElementById(mappingId);
      Mapping mapping = parent.getData();
      OutputMessage outputMessage = mapping.getOutputMessage();
      outputMessage.setLabel(value);
      getConfiguration().put(mapping.getInputMessage(), outputMessage);
    }
  }

  @Override
  protected void doLoad() {
    addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "body", mapperView.render()));
    NavigableSet<Integer> devices = request(Services.getDeviceIndexes);
    for (Integer deviceIndex : devices) {
      addDevice(deviceIndex);
    }
    mapperView.displayDeviceSelector(deviceControllers);
    if (deviceControllers.size() > 0) {
      displayMappings(deviceControllers.firstKey());
    }
  }

  @Override
  protected void doMove(String id, String value) {
    System.out.println("doMove: id=" + id + ", value=" + value);
    if (id.startsWith("mapping-")) {
      Parent parent = mapperView.getElementById(id);
      Mapping mapping = parent.getData();
      OutputMessage outputMessage = mapping.getOutputMessage();
      Map<String, String> map = JsonUtilities.toMap(value);
      double x = Double.parseDouble(map.get("x"));
      outputMessage.setX(x);
      double y = Double.parseDouble(map.get("y"));
      outputMessage.setY(y);
      getConfiguration().put(mapping.getInputMessage(), outputMessage);
    }
  }

  private void addDevice(Integer deviceIndex) {
    Controller controller = request(new DeviceControllerService(deviceIndex));
    if (controller instanceof MidiController) {
      deviceControllers.put(deviceIndex, controller);
    }
  }

  private void displayMappings(int deviceIndex) {
    this.deviceIndex = deviceIndex;
    Controller controller = deviceControllers.get(deviceIndex);
    MidiConfiguration configuration = (MidiConfiguration) controller.getConfiguration();
    NavigableMap<InputMessage, OutputMessage> inputMap = configuration.getInputMap();
    for (Entry<InputMessage, OutputMessage> entry : inputMap.entrySet()) {
      mapperView.displayMapping(entry.getKey(), entry.getValue());
    }
  }

  private void doCommand(OnCommand message) {
  }

  private void doDeviceAttached(OnDeviceAttached message) {
    addDevice(message.getDeviceIndex());
    mapperView.displayDeviceSelector(deviceControllers);
  }

  private void doDeviceCommand(OnDeviceCommand message) {
  }

  private void doDeviceDetached(OnDeviceDetached message) {
    deviceControllers.remove(message.getDeviceIndex());
  }

  private void doShortMessage(OnShortMessage message) {
    if (message.getDeviceIndex() == deviceIndex) {
      ShortMessage shortMessage = message.getShortMessage();
      int command = shortMessage.getCommand();
      if (command == ShortMessage.NOTE_ON || command == ShortMessage.CONTROL_CHANGE || command == ShortMessage.PITCH_BEND) {
        InputMessage inputMessage = new InputMessage(shortMessage);
        Controller controller = deviceControllers.get(deviceIndex);
        MidiConfiguration configuration = (MidiConfiguration) controller.getConfiguration();
        OutputMessage outputMessage = configuration.get(inputMessage);
        if (outputMessage == null) {
          int control = inputMessage.getControl();
          InputType inputType;
          if (inputMessage.isKey()) {
            inputType = InputType.KEY;
          } else {
            inputType = InputType.ROTARY;
          }
          OutputType outputType = OutputType.DEFAULT;
          String label = Names.formatNoteName(control);
          outputMessage = new OutputMessage(inputType, outputType, control, label);
          getConfiguration().put(inputMessage, outputMessage);
        }
        String id = mapperView.getMappingId(inputMessage);
        Parent mapping = mapperView.getElementById(id);
        if (mapping != null) {
          doClick(id);
        } else {
          mapperView.displayMapping(inputMessage, outputMessage);
        }
      }
    }
  }

  private MidiConfiguration getConfiguration() {
    Controller controller = deviceControllers.get(deviceIndex);
    MidiConfiguration configuration = (MidiConfiguration) controller.getConfiguration();
    return configuration;
  }

}
