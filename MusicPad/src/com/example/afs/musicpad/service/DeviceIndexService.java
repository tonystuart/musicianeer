package com.example.afs.musicpad.service;

public class DeviceIndexService {
  private int deviceIndex;

  public DeviceIndexService(int deviceIndex) {
    this.deviceIndex = deviceIndex;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DeviceIndexService other = (DeviceIndexService) obj;
    if (deviceIndex != other.deviceIndex) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + deviceIndex;
    return result;
  }
}
