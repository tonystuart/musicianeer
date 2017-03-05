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

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.message.DeviceAttached;
import com.example.afs.musicpad.message.DeviceDetached;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.BrokerTask;

public class DeviceManager extends BrokerTask<Message> {

  private Map<String, DeviceHandler> deviceHandlers = new HashMap<>();
  private Synthesizer synthesizer;

  public DeviceManager(Broker<Message> messageBroker, Synthesizer synthesizer) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    subscribe(DeviceAttached.class, message -> onDeviceAttach(message.getDevice()));
    subscribe(DeviceDetached.class, message -> onDeviceDetach(message.getDevice()));
  }

  private void onDeviceAttach(String newDevice) {
    System.out.println("DeviceManager.onDeviceAttach: adding newDevice=" + newDevice);
    DeviceHandler deviceHandler = new DeviceHandler(getBroker(), synthesizer, newDevice);
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
