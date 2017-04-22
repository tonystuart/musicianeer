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
import java.util.Map.Entry;
import java.util.Set;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.device.common.ControllableGroup;
import com.example.afs.musicpad.device.common.Device;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.player.Player.KeyType;
import com.example.afs.musicpad.player.Player.MappingType;
import com.example.afs.musicpad.player.Player.UnitType;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;

public class QwertyWatcher extends BrokerTask<Message> {

  private Synthesizer synthesizer;
  private Map<String, ControllableGroup> oldDevices = new HashMap<>();

  public QwertyWatcher(Broker<Message> broker, Synthesizer synthesizer) {
    super(broker, 1000);
    this.synthesizer = synthesizer;
  }

  @Override
  public void onTimeout() throws InterruptedException {
    Set<String> newDevices = getDevices();
    Iterator<Entry<String, ControllableGroup>> oldIterator = oldDevices.entrySet().iterator();
    while (oldIterator.hasNext()) {
      Entry<String, ControllableGroup> oldDevice = oldIterator.next();
      if (!newDevices.contains(oldDevice.getKey())) {
        detachDevice(oldDevice.getKey(), oldDevice.getValue());
        oldIterator.remove();
      }
    }
    for (String newDevice : newDevices) {
      if (!oldDevices.containsKey(newDevice)) {
        attachDevice(newDevice);
      }
    }
  }

  private void attachDevice(String name) {
    System.out.println("Attaching QWERY device " + name);
    Device device = new Device(name);
    device.setMappingType(MappingType.ALPHA);
    device.setKeyType(KeyType.INSTRUMENT);
    device.setUnitType(UnitType.NOTE);
    DeviceHandler deviceHandler = new DeviceHandler(getBroker(), synthesizer, device);
    QwertyReader qwertyReader = new QwertyReader(deviceHandler.getInputQueue(), name);
    ControllableGroup controllableGroup = new ControllableGroup(deviceHandler, qwertyReader);
    oldDevices.put(name, controllableGroup);
    controllableGroup.start();
  }

  private void detachDevice(String name, ControllableGroup controllableGroup) {
    System.out.println("Detaching QWERTY device " + name);
    controllableGroup.terminate();
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