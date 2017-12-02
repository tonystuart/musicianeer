// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.mapper;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;

import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.device.common.Controller;
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
  }

  @Override
  protected void doClick(String id) {
    if (id.startsWith("device-")) {
      mapperView.selectElement(id, "selected-device");
      deviceIndex = Integer.parseInt(id.substring("device-".length()));
    }
  }

  @Override
  protected void doInput(String id, String value) {
    System.out.println("id=" + id + ", value=" + value);
    if (id.equals("mapping")) {
      mapperView.selectCommand();
    } else if (id.startsWith("group-")) {
      mapperView.selectGroup();
    } else if (id.startsWith("sound-")) {
      mapperView.selectSound();
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
    mapperView.renderMessageDetails("NONE", 0, 0, 0);
  }

  private void doCommand(OnCommand message) {
  }

  private void doDeviceCommand(OnDeviceCommand message) {
  }

  private void doShortMessage(OnShortMessage message) {
    if (message.getDeviceIndex() == deviceIndex) {
      ShortMessage shortMessage = message.getShortMessage();
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

}
