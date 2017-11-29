package com.example.afs.musicpad.service;

import com.example.afs.musicpad.task.ServiceTask.Service;

public class BackgroundMuteService extends DeviceIndexService implements Service<Boolean> {
  public BackgroundMuteService(int deviceIndex) {
    super(deviceIndex);
  }
}
