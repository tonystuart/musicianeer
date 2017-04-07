// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.device.midi.MidiConfiguration.ChannelMessage;

public class OnDeviceMessage implements Message {

  private ChannelMessage deviceMessage;

  public OnDeviceMessage(ChannelMessage deviceMessage) {
    this.deviceMessage = deviceMessage;
  }

  public ChannelMessage getDeviceMessage() {
    return deviceMessage;
  }

  @Override
  public String toString() {
    return "OnDeviceMessage [deviceMessage=" + deviceMessage + "]";
  }

}
