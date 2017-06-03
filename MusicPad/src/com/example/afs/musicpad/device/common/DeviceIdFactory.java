// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

import java.util.HashMap;
import java.util.Map;

public class DeviceIdFactory {
  private static int nextDeviceIndex;
  private static Map<String, Integer> devices = new HashMap<>();

  public static synchronized int getDeviceIndex(String name) {
    Integer deviceIndex = devices.get(name);
    if (deviceIndex == null) {
      deviceIndex = nextDeviceIndex++;
      devices.put(name, deviceIndex);
    }
    return deviceIndex;
  }

}
