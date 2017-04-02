// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

public class DeviceGroup {

  public interface DeviceInterface {
    public void start();

    public void terminate();
  }

  private DeviceHandler deviceHandler;
  private DeviceInterface deviceInterface;

  public DeviceGroup(DeviceHandler deviceHandler, DeviceInterface deviceInterface) {
    this.deviceHandler = deviceHandler;
    this.deviceInterface = deviceInterface;
  }

  public void start() {
    deviceHandler.start();
    deviceInterface.start();
  }

  public void terminate() {
    deviceHandler.terminate();
    deviceInterface.terminate();
  }

}
