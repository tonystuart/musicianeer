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

  private static class Device {
    private int deviceIndex;
    private Controller controller;
    private DeviceHandler deviceHandler;

    public Device(int deviceIndex, Controller controller, DeviceHandler deviceHandler) {
      this.deviceIndex = deviceIndex;
      this.controller = controller;
      this.deviceHandler = deviceHandler;
    }

    public Controller getController() {
      return controller;
    }

    public int getDeviceIndex() {
      return deviceIndex;
    }

    public void start() {
      deviceHandler.setController(controller);
      controller.setDeviceHandler(deviceHandler);
      deviceHandler.start();
      controller.start();
    }

    public void terminate() {
      deviceHandler.terminate();
      controller.terminate();
    }
  }

  private Synthesizer synthesizer;
  private WatcherBehavior watcherBehavior;
  private Map<String, Device> oldDevices = new HashMap<>();
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
    Iterator<Entry<String, Device>> oldIterator = oldDevices.entrySet().iterator();
    while (oldIterator.hasNext()) {
      Entry<String, Device> oldEntry = oldIterator.next();
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
    Device device = new Device(deviceIndex, controller, deviceHandler);
    oldDevices.put(deviceName, device);
    device.start();
    publish(new OnDeviceAttached(deviceIndex));
  }

  private void detachDevice(String name, Device device) {
    watcherBehavior.detachDevice(name, device.getController());
    device.terminate();
    publish(new OnDeviceDetached(device.getDeviceIndex()));
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
    Entry<String, Device> entry = findByDeviceIndex(parameter);
    if (entry != null) {
      String deviceName = entry.getKey();
      Device device = entry.getValue();
      detachDevice(deviceName, device);
      detachedDevices.add(deviceName);
      oldDevices.remove(deviceName);
    }
  }

  private void doReattach() {
    detachedDevices.clear();
  }

  private Entry<String, Device> findByDeviceIndex(int deviceIndex) {
    for (Entry<String, Device> entry : oldDevices.entrySet()) {
      if (entry.getValue().getDeviceIndex() == deviceIndex) {
        return entry;
      }
    }
    return null;
  }

}