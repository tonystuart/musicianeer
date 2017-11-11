package com.example.afs.musicpad.player;

import com.example.afs.musicpad.task.ServiceTask.Service;

public class PlayerVelocityService implements Service<Integer> {
  private int deviceIndex;

  public PlayerVelocityService(int deviceIndex) {
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
    PlayerVelocityService other = (PlayerVelocityService) obj;
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
