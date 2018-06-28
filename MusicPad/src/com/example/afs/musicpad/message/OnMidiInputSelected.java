// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.task.Message;

public class OnMidiInputSelected implements Message {

  private int channel;
  private int deviceIndex;

  public OnMidiInputSelected(int channel, int deviceIndex) {
    this.channel = channel;
    this.deviceIndex = deviceIndex;
  }

  public int getChannel() {
    return channel;
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  @Override
  public String toString() {
    return "OnMidiInputSelected [channel=" + channel + ", deviceIndex=" + deviceIndex + "]";
  }

}
