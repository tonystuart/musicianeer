// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

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

  @Override
  public String toString() {
    return "DeviceIndexService [deviceIndex=" + deviceIndex + ", class=" + getClass() + "]";
  }
}
