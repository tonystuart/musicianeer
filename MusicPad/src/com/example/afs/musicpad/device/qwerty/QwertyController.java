// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.device.common.DeviceHandler;

public class QwertyController implements Controller {

  private String deviceName;
  private DeviceHandler deviceHandler;
  private QwertyReader qwertyReader;

  public QwertyController(String deviceName) {
    this.deviceName = deviceName;
  }

  @Override
  public DeviceHandler getDeviceHandler() {
    return deviceHandler;
  }

  public String getDeviceName() {
    return deviceName;
  }

  @Override
  public void setDeviceHandler(DeviceHandler deviceHandler) {
    this.deviceHandler = deviceHandler;
  }

  @Override
  public void start() {
    if (deviceHandler == null) {
      throw new IllegalStateException();
    }
    qwertyReader = new QwertyReader(deviceHandler, this);
    qwertyReader.start();
  }

  @Override
  public void terminate() {
    qwertyReader.terminate();
  }

}