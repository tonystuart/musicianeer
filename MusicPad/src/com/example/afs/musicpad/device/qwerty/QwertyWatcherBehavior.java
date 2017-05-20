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
import java.util.Map;

import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.device.common.DeviceBundle;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.device.common.WatcherBehavior;

public class QwertyWatcherBehavior implements WatcherBehavior {

  @Override
  public Controller attachDevice(DeviceHandler deviceHandler, DeviceBundle deviceBundle) {
    System.out.println("Attaching QWERTY device " + deviceHandler.getDeviceName());
    Controller controller = new QwertyController(deviceHandler, deviceBundle);
    return controller;
  }

  @Override
  public void detachDevice(String name, Controller controller) {
    System.out.println("Detaching QWERTY device " + name);
  }

  @Override
  public Map<String, DeviceBundle> getDevices() {
    // See: man udev
    // See: http://reactivated.net/writing_udev_rules.html
    // See: https://puredata.info/docs/faq/how-can-i-set-permissions-so-hid-can-read-devices-in-gnu-linux
    File deviceFolder = new File("/dev/input/by-path");
    File[] deviceArray = deviceFolder.listFiles((dir, name) -> isMusicPad(name));
    Map<String, DeviceBundle> devices = new HashMap<>();
    for (File deviceFile : deviceArray) {
      devices.put(deviceFile.getPath(), new DeviceBundle() {
        // QwertyController uses the key, not the value
      });
    }
    return devices;
  }

  private boolean isMusicPad(String name) {
    return name.endsWith("event-kbd");
  }

}