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

public class Device {

  private static int nextDeviceIndex;
  private static Map<String, Integer> devices = new HashMap<>();

  private static synchronized int getDeviceIndex(String name) {
    Integer deviceIndex = devices.get(name);
    if (deviceIndex == null) {
      deviceIndex = nextDeviceIndex++;
      devices.put(name, deviceIndex);
    }
    return deviceIndex;
  }

  private final String name;
  private final int index;
  private int channel;
  private InputMapping inputMapping;

  public Device(String name) {
    this.name = name;
    this.index = getDeviceIndex(name);
  }

  public int getChannel() {
    return channel;
  }

  public int getIndex() {
    return index;
  }

  public InputMapping getInputMapping() {
    return inputMapping;
  }

  public String getName() {
    return name;
  }

  public void setChannel(int channel) {
    this.channel = channel;
  }

  public void setInputMapping(InputMapping inputMapping) {
    this.inputMapping = inputMapping;
  }

  @Override
  public String toString() {
    return "Device [name=" + name + ", index=" + index + ", channel=" + channel + "]";
  }

}
