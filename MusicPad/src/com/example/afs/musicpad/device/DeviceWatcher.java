// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnDeviceAttached;
import com.example.afs.musicpad.message.OnDeviceDetached;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;

public class DeviceWatcher extends BrokerTask<Message> {

  private Set<String> oldDevices = new HashSet<>();

  public DeviceWatcher(Broker<Message> messageBroker) {
    super(messageBroker, 1000);
  }

  @Override
  public void onTimeout() throws InterruptedException {
    Set<String> newDevices = getDevices();
    Iterator<String> oldIterator = oldDevices.iterator();
    while (oldIterator.hasNext()) {
      String oldDevice = oldIterator.next();
      if (!newDevices.contains(oldDevice)) {
        publish(new OnDeviceDetached(oldDevice));
        oldIterator.remove();
      }
    }
    for (String newDevice : newDevices) {
      if (!oldDevices.contains(newDevice)) {
        publish(new OnDeviceAttached(newDevice));
        oldDevices.add(newDevice);
      }
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
    return name.endsWith("event-kbd") && !name.startsWith("platform");
  }

}