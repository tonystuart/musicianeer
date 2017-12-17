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
import com.example.afs.musicpad.webapp.karaoke.Utils;

public class MapperController extends ControllerTask {

  private int deviceIndex;
  private MapperView mapperView;
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
      Parent mapping = mapperView.getElementById(mappingId);
      MappingData mappingData = mapping.getData();
      OutputMessage outputMessage = mappingData.getOutputMessage();
      outputMessage.setInputType(InputType.values()[Integer.parseInt(value)]);
      getConfiguration().put(mappingData.getInputMessage(), outputMessage);
    } else if (id.startsWith("output-type-mapping-")) {
      String mappingId = id.substring("output-type-".length());
      Parent mapping = mapperView.getElementById(mappingId);
      MappingData mappingData = mapping.getData();
      OutputMessage outputMessage = mappingData.getOutputMessage();
      OutputType outputType = OutputType.values()[Integer.parseInt(value)];
      outputMessage.setOutputType(outputType);
      mapperView.setOutputType(mappingId, outputType);
      getConfiguration().put(mappingData.getInputMessage(), outputMessage);
    } else if (id.startsWith("index-mapping-")) {
      String mappingId = id.substring("index-".length());
      Parent mapping = mapperView.getElementById(mappingId);
      MappingData mappingData = mapping.getData();
      OutputMessage outputMessage = mappingData.getOutputMessage();
      int index = Utils.parseInt(value, -1);
      if (index >= 0) {
        outputMessage.setIndex(Integer.parseInt(value));
        getConfiguration().put(mappingData.getInputMessage(), outputMessage);
      }
    } else if (id.startsWith("label-")) {
      String mappingId = id.substring("label-".length());
      Parent mapping = mapperView.getElementById(mappingId);
      MappingData mappingData = mapping.getData();
      OutputMessage outputMessage = mappingData.getOutputMessage();
      if (value.length() > 0) {
        outputMessage.setLabel(value);
        getConfiguration().put(mappingData.getInputMessage(), outputMessage);
      }
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
    //    if (deviceControllers.size() > 0) {
    //      displayMappings(deviceControllers.firstKey());
    //    }
  }

  @Override
  protected void doMove(String id, String value) {
    System.out.println("doMove: id=" + id + ", value=" + value);
    if (id.startsWith("mapping-")) {
      Parent mapping = mapperView.getElementById(id);
      MappingData mappingData = mapping.getData();
      OutputMessage outputMessage = mappingData.getOutputMessage();
      Map<String, String> map = JsonUtilities.toMap(value);
      double x = Double.parseDouble(map.get("x"));
      outputMessage.setX(x);
      double y = Double.parseDouble(map.get("y"));
      outputMessage.setY(y);
      getConfiguration().put(mappingData.getInputMessage(), outputMessage);
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
    mapperView.displayMappings(inputMap);
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
      InputMessage inputMessage = new InputMessage(shortMessage);
      String id = mapperView.getMappingId(inputMessage);
      Parent mapping = mapperView.getElementById(id);
      if (mapping == null) {
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
        mapperView.displayMapping(inputMessage, outputMessage);
      }
      mapperView.selectMapping(id, inputMessage, shortMessage);
    }
  }

  private MidiConfiguration getConfiguration() {
    Controller controller = deviceControllers.get(deviceIndex);
    MidiConfiguration configuration = (MidiConfiguration) controller.getConfiguration();
    return configuration;
  }

}
