// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.DeviceCommand;

public class OnDeviceCommand extends TypedMessage {

  private DeviceCommand deviceCommand;
  private int deviceIndex;
  private int parameter;

  public OnDeviceCommand() {
  }

  public OnDeviceCommand(DeviceCommand deviceCommand, int deviceIndex, int parameter) {
    this.deviceCommand = deviceCommand;
    this.deviceIndex = deviceIndex;
    this.parameter = parameter;
  }

  public DeviceCommand getDeviceCommand() {
    return deviceCommand;
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  public int getParameter() {
    return parameter;
  }

  public void setDeviceCommand(DeviceCommand deviceCommand) {
    this.deviceCommand = deviceCommand;
  }

  public void setDeviceIndex(int deviceIndex) {
    this.deviceIndex = deviceIndex;
  }

  public void setParameter(int parameter) {
    this.parameter = parameter;
  }

  @Override
  public String toString() {
    return "OnDeviceCommand [deviceCommand=" + deviceCommand + ", deviceIndex=" + deviceIndex + ", parameter=" + parameter + "]";
  }

}
