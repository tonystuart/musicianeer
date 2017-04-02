// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;

public class QwertyWatcher extends BrokerTask<Message> {

  private Synthesizer synthesizer;
  private Set<String> oldDevices = new HashSet<>();
  private Map<String, DeviceHandler> deviceHandlers = new HashMap<>();

  public QwertyWatcher(Broker<Message> messageBroker, Synthesizer synthesizer) {
    super(messageBroker, 1000);
    this.synthesizer = synthesizer;
  }

  @Override
  public void onTimeout() throws InterruptedException {
    Set<String> newDevices = getDevices();
    Iterator<String> oldIterator = oldDevices.iterator();
    while (oldIterator.hasNext()) {
      String oldDevice = oldIterator.next();
      if (!newDevices.contains(oldDevice)) {
        detachDevice(oldDevice);
        oldIterator.remove();
      }
    }
    for (String newDevice : newDevices) {
      if (!oldDevices.contains(newDevice)) {
        attachDevice(newDevice);
        oldDevices.add(newDevice);
      }
    }
  }

  private void attachDevice(String newDevice) {
    System.out.println("DeviceManager.onDeviceAttach: adding newDevice=" + newDevice);
    DeviceHandler deviceHandler = new DeviceHandler(getBroker(), synthesizer, newDevice);
    deviceHandlers.put(newDevice, deviceHandler);
    deviceHandler.start();
  }

  private void detachDevice(String oldDevice) {
    System.out.println("DeviceManager.onDeviceDetach: removing oldDevice=" + oldDevice);
    DeviceHandler deviceHandler = deviceHandlers.get(oldDevice);
    if (deviceHandler != null) {
      deviceHandler.terminate();
    }
  }

  private Set<String> getDevices() {
    // See: man udev
    // See: http://reactivated.net/writing_udev_rules.html
    // See: https://puredata.info/docs/faq/how-can-i-set-permissions-so-hid-can-read-devices-in-gnu-linux
    File deviceFolder = new File("/dev/input/by-path");
    File[] deviceArray = deviceFolder.listFiles((dir, name) -> isMusicPad(name));
    Set<String> deviceSet = new HashSet<>();
    for (File device : deviceArray) {
      deviceSet.add(device.getPath());
    }
    return deviceSet;
  }

  private boolean isMusicPad(String name) {
    return name.endsWith("event-kbd");
  }

}