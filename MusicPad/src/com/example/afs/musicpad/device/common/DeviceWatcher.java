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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceAttached;
import com.example.afs.musicpad.message.OnDeviceDetached;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;

public class DeviceWatcher extends BrokerTask<Message> {

  private Synthesizer synthesizer;
  private WatcherBehavior watcherBehavior;
  private Map<String, Controller> oldDevices = new HashMap<>();

  public DeviceWatcher(Broker<Message> broker, Synthesizer synthesizer, WatcherBehavior watcherBehavior) {
    super(broker, 1000);
    this.synthesizer = synthesizer;
    this.watcherBehavior = watcherBehavior;
    subscribe(OnCommand.class, message -> doCommand(message.getCommand(), message.getParameter()));
  }

  @Override
  public void onTimeout() throws InterruptedException {
    Map<String, DeviceBundle> newDevices = watcherBehavior.getDevices();
    Iterator<Entry<String, Controller>> oldIterator = oldDevices.entrySet().iterator();
    while (oldIterator.hasNext()) {
      Entry<String, Controller> oldEntry = oldIterator.next();
      if (!newDevices.containsKey(oldEntry.getKey())) {
        detachDevice(oldEntry.getKey(), oldEntry.getValue());
        oldIterator.remove();
      }
    }
    for (Entry<String, DeviceBundle> newEntry : newDevices.entrySet()) {
      if (!oldDevices.containsKey(newEntry.getKey())) {
        attachDevice(newEntry.getKey(), newEntry.getValue());
      }
    }
  }

  private void attachDevice(String name, DeviceBundle deviceBundle) {
    DeviceHandler deviceHandler = new DeviceHandler(getBroker(), synthesizer, name);
    Controller controller = watcherBehavior.attachDevice(deviceHandler, deviceBundle);
    oldDevices.put(name, controller);
    controller.start();
    publish(new OnDeviceAttached(deviceHandler.getIndex()));
  }

  private void detachDevice(String name, Controller controller) {
    watcherBehavior.detachDevice(name, controller);
    controller.terminate();
    publish(new OnDeviceDetached(controller.getIndex()));
  }

  private void doCommand(Command command, int index) {
    if (command == Command.DETACH) {
      Entry<String, Controller> entry = findByControllerIndex(index);
      if (entry != null) {
        detachDevice(entry.getKey(), entry.getValue());
        // Leave device in list of oldDevices to prevent re-attachment
      }
    }
  }

  private Entry<String, Controller> findByControllerIndex(int index) {
    for (Entry<String, Controller> entry : oldDevices.entrySet()) {
      if (entry.getValue().getIndex() == index) {
        return entry;
      }
    }
    return null;
  }

}