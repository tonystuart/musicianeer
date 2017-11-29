package com.example.afs.musicpad.service;

import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.task.ServiceTask.Service;

public class DeviceControllerService extends DeviceIndexService implements Service<Controller> {
  public DeviceControllerService(int deviceIndex) {
    super(deviceIndex);
  }
}
