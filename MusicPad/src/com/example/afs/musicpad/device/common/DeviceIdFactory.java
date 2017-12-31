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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.afs.musicpad.util.JsonUtilities;

public class DeviceIdFactory {

  private static class DeviceIdManager {

    private Devices devices;
    private int nextDeviceIndex;
    private Set<Integer> deviceIndexes = new HashSet<>();

    private DeviceIdManager() {
      devices = JsonUtilities.fromJsonFile(DEVICES, Devices.class);
      if (devices == null) {
        devices = new Devices(new HashMap<>());
      } else {
        deviceIndexes.addAll(devices.getDevices().values());
      }
    }

    public synchronized int getDeviceIndex(String name) {
      Integer deviceIndex = devices.getDevices().get(name);
      if (deviceIndex == null) {
        deviceIndex = getNextDeviceIndex();
        devices.getDevices().put(name, deviceIndex);
        deviceIndexes.add(deviceIndex);
        JsonUtilities.toJsonFile(DEVICES, devices);
      }
      return deviceIndex;
    }

    private int getNextDeviceIndex() {
      while (deviceIndexes.contains(nextDeviceIndex)) {
        nextDeviceIndex++;
      }
      return nextDeviceIndex;
    }
  }

  private static class Devices {

    private Map<String, Integer> devices;

    public Devices(HashMap<String, Integer> devices) {
      this.devices = devices;
    }

    public Map<String, Integer> getDevices() {
      return devices;
    }

    @Override
    public String toString() {
      return "Devices [devices=" + devices + "]";
    }

  }

  private static final String DEVICES = "devices";
  private static final DeviceIdManager INSTANCE = new DeviceIdManager();

  public static int getDeviceIndex(String name) {
    return INSTANCE.getDeviceIndex(name);
  }

}
