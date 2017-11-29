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

import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.message.OnShadowUpdate.Action;
import com.example.afs.musicpad.service.DeviceControllerService;
import com.example.afs.musicpad.service.Services;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.MessageBroker;

public class MapperController extends ControllerTask {

  private MapperView mapperView;
  private NavigableMap<Integer, Controller> deviceControllers = new TreeMap<>();

  public MapperController(MessageBroker broker) {
    super(broker);
    mapperView = new MapperView(this);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnDeviceCommand.class, message -> doDeviceCommand(message));
  }

  @Override
  public void start() {
    super.start();
    // put MIDI devices in verbose mode. Handle multiple instances of this app.
    NavigableSet<Integer> devices = request(Services.getDeviceIndexes);
    for (Integer deviceIndex : devices) {
      Controller controller = request(new DeviceControllerService(deviceIndex));
      deviceControllers.put(deviceIndex, controller);
    }
    mapperView.renderDeviceList(deviceControllers);
  }

  @Override
  protected void doClick(String id) {
    if (id.startsWith("device-")) {
      mapperView.selectElement(id, "selected-device");
    }
  }

  @Override
  protected void doInput(String id, int value) {
  }

  @Override
  protected void doLoad() {
    addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "body", mapperView.render()));
  }

  private void doCommand(OnCommand message) {
  }

  private void doDeviceCommand(OnDeviceCommand message) {
  }

}
