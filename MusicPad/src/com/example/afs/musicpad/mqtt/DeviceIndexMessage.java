// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.mqtt;

public class DeviceIndexMessage {
  private int deviceIndex;

  public DeviceIndexMessage() {
  }

  public DeviceIndexMessage(int deviceIndex) {
    this.deviceIndex = deviceIndex;
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  @Override
  public String toString() {
    return "DeviceIndexMessage [deviceIndex=" + deviceIndex + "]";
  }

}