// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.util.MessageBroker.Message;

public class DeviceAttach implements Message {
  private int deviceId;
  private String device;

  public DeviceAttach(int deviceId, String device) {
    this.deviceId = deviceId;
    this.device = device;
  }

  public String getDevice() {
    return device;
  }

  public int getDeviceId() {
    return deviceId;
  }

  @Override
  public String toString() {
    return "DeviceAttach [deviceId=" + deviceId + ", device=" + device + "]";
  }
}