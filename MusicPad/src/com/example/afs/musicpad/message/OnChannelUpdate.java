// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.device.common.DeviceHandler.InputType;
import com.example.afs.musicpad.device.common.DeviceHandler.OutputType;
import com.example.afs.musicpad.keycap.KeyCapMap;

public class OnChannelUpdate extends Message {

  private int deviceIndex;
  private String deviceName;
  private int channel;
  private InputType inputType;
  private OutputType outputType;
  private KeyCapMap keyCapMap;

  public OnChannelUpdate(int deviceIndex, String deviceName, int channel, InputType inputType, OutputType outputType, KeyCapMap keyCapMap) {
    this.deviceIndex = deviceIndex;
    this.deviceName = deviceName;
    this.channel = channel;
    this.inputType = inputType;
    this.outputType = outputType;
    this.keyCapMap = keyCapMap;
  }

  public int getChannel() {
    return channel;
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public InputType getInputType() {
    return inputType;
  }

  public KeyCapMap getKeyCapMap() {
    return keyCapMap;
  }

  public OutputType getOutputType() {
    return outputType;
  }

  @Override
  public String toString() {
    return "OnChannelUpdate [deviceIndex=" + deviceIndex + ", deviceName=" + deviceName + ", channel=" + channel + ", inputType=" + inputType + ", outputType=" + outputType + ", keyCapMap=" + keyCapMap + "]";
  }

}
