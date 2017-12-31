// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.service;

import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.task.ServiceTask.Service;

public class DeviceControllerService extends DeviceIndexService implements Service<Controller> {
  public DeviceControllerService(int deviceIndex) {
    super(deviceIndex);
  }
}
