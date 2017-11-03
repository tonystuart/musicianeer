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
import com.example.afs.musicpad.playable.PlayableMap;

public class OnChannelUpdate extends TypedMessage {

  private int deviceIndex;
  private String deviceName;
  private int channel;
  private InputType inputType;
  private OutputType outputType;
  private PlayableMap playableMap;

  public OnChannelUpdate(int deviceIndex, String deviceName, int channel, InputType inputType, OutputType outputType, PlayableMap playableMap) {
    this.deviceIndex = deviceIndex;
    this.deviceName = deviceName;
    this.channel = channel;
    this.inputType = inputType;
    this.outputType = outputType;
    this.playableMap = playableMap;
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

  public OutputType getOutputType() {
    return outputType;
  }

  public PlayableMap getPlayableMap() {
    return playableMap;
  }

  @Override
  public String toString() {
    return "OnChannelUpdate [deviceIndex=" + deviceIndex + ", deviceName=" + deviceName + ", channel=" + channel + ", inputType=" + inputType + ", outputType=" + outputType + ", playableMap=" + playableMap + "]";
  }

}
