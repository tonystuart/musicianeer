// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import java.util.List;

import com.example.afs.musicpad.device.midi.MidiConfiguration.ChannelMessage;

public class OnDeviceMessages implements Message {

  private List<ChannelMessage> deviceMessages;

  public OnDeviceMessages(List<ChannelMessage> deviceMessages) {
    this.deviceMessages = deviceMessages;
  }

  public List<ChannelMessage> getDeviceMessages() {
    return deviceMessages;
  }

  @Override
  public String toString() {
    return "OnDeviceMessages [deviceMessages=" + deviceMessages + "]";
  }

}
