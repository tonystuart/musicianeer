// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad;

import java.util.HashMap;
import java.util.Map;

import com.example.afs.musicpad.message.DeviceAttach;
import com.example.afs.musicpad.message.DeviceDetach;
import com.example.afs.musicpad.util.MessageBroker;
import com.example.afs.musicpad.util.Task;

public class DeviceManager extends Task {

  private Map<String, DeviceHandler> deviceHandlers = new HashMap<>();

  public DeviceManager(MessageBroker messageBroker) {
    super(messageBroker);
    subscribe(DeviceAttach.class, message -> onDeviceAttach(message.getNewDevice()));
    subscribe(DeviceDetach.class, message -> onDeviceDetach(message.getOldDevice()));
  }

  private void onDeviceAttach(String newDevice) {
    System.out.println("DeviceManager.onDeviceAttach: adding newDevice=" + newDevice);
    DeviceHandler deviceHandler = new DeviceHandler(getMessageBroker(), newDevice);
    deviceHandlers.put(newDevice, deviceHandler);
    deviceHandler.start();
  }

  private void onDeviceDetach(String oldDevice) {
    System.out.println("DeviceManager.onDeviceDetach: removing oldDevice=" + oldDevice);
    DeviceHandler deviceHandler = deviceHandlers.get(oldDevice);
    if (deviceHandler != null) {
      deviceHandler.terminate();
    }
  }
}
