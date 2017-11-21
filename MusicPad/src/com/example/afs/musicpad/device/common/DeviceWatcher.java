// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.device.common.DeviceHandler.InputType;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceAttached;
import com.example.afs.musicpad.message.OnDeviceDetached;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.MessageTask;

public class DeviceWatcher extends MessageTask {

  private Synthesizer synthesizer;
  private WatcherBehavior watcherBehavior;
  private Map<String, Controller> oldDevices = new HashMap<>();
  private Set<String> detachedDevices = new HashSet<>();

  public DeviceWatcher(MessageBroker broker, Synthesizer synthesizer, WatcherBehavior watcherBehavior) {
    super(broker, 1000);
    this.synthesizer = synthesizer;
    this.watcherBehavior = watcherBehavior;
    subscribe(OnCommand.class, message -> doCommand(message));
  }

  @Override
  public void onTimeout() throws InterruptedException {
    Set<String> newDeviceNames = watcherBehavior.getDeviceNames();
    Iterator<Entry<String, Controller>> oldIterator = oldDevices.entrySet().iterator();
    while (oldIterator.hasNext()) {
      Entry<String, Controller> oldEntry = oldIterator.next();
      if (!newDeviceNames.contains(oldEntry.getKey())) {
        detachDevice(oldEntry.getKey(), oldEntry.getValue());
        oldIterator.remove();
      }
    }
    for (String newDeviceName : newDeviceNames) {
      if (!oldDevices.containsKey(newDeviceName) && !detachedDevices.contains(newDeviceName)) {
        attachDevice(newDeviceName);
      }
    }
  }

  private void attachDevice(String deviceName) {
    int deviceIndex = DeviceIdFactory.getDeviceIndex(deviceName);
    InputType inputType = watcherBehavior.getInputType();
    DeviceHandler deviceHandler = new DeviceHandler(getBroker(), synthesizer, deviceIndex, inputType);
    Controller controller = watcherBehavior.attachDevice(deviceName);
    oldDevices.put(deviceName, controller);
    deviceHandler.setController(controller);
    controller.setDeviceHandler(deviceHandler);
    deviceHandler.start();
    controller.start();
    publish(new OnDeviceAttached(deviceIndex));
  }

  private void detachDevice(String name, Controller controller) {
    watcherBehavior.detachDevice(name, controller);
    controller.getDeviceHandler().terminate();
    controller.terminate();
    publish(new OnDeviceDetached(controller.getDeviceHandler().getDeviceIndex()));
  }

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    switch (command) {
    case DETACH:
      doDetach(message.getParameter());
      break;
    case RESET:
      doReattach();
      break;
    default:
      break;
    }
  }

  private void doDetach(int parameter) {
    Entry<String, Controller> entry = findByControllerDeviceIndex(parameter);
    if (entry != null) {
      String deviceName = entry.getKey();
      Controller controller = entry.getValue();
      detachDevice(deviceName, controller);
      detachedDevices.add(deviceName);
      oldDevices.remove(deviceName);
    }
  }

  private void doReattach() {
    detachedDevices.clear();
  }

  private Entry<String, Controller> findByControllerDeviceIndex(int deviceIndex) {
    for (Entry<String, Controller> entry : oldDevices.entrySet()) {
      if (entry.getValue().getDeviceHandler().getDeviceIndex() == deviceIndex) {
        return entry;
      }
    }
    return null;
  }

}