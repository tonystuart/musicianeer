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
  private QwertyConfiguration qwertyConfiguration;

  public QwertyController(DeviceHandler deviceHandler, String deviceName) {
    this.deviceHandler = deviceHandler;
    this.deviceName = deviceName;
    this.qwertyConfiguration = new QwertyConfiguration(deviceHandler.tsGetBroker(), deviceHandler.tsGetDeviceIndex());
  }

  @Override
  public QwertyConfiguration getConfiguration() {
    return qwertyConfiguration;
  }

  @Override
  public String getDeviceName() {
    return deviceName;
  }

  @Override
  public void start() {
    qwertyReader = new QwertyReader(deviceHandler, this);
    qwertyReader.start();
  }

  @Override
  public void terminate() {
    qwertyReader.terminate();
  }

}