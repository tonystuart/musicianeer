package com.example.afs.musicpad.service;

import com.example.afs.musicpad.task.ServiceTask.Service;

public class PlayerVelocityService extends DeviceIndexService implements Service<Integer> {
  public PlayerVelocityService(int deviceIndex) {
    super(deviceIndex);
  }
}
