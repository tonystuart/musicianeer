// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.message;

import com.example.afs.musicianeer.task.Message;

public class OnMidiOutputSelected implements Message {

  private int channel;
  private int deviceIndex;

  public OnMidiOutputSelected(int channel, int deviceIndex) {
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
    return "OnMidiOutputSelected [channel=" + channel + ", deviceIndex=" + deviceIndex + "]";
  }

}
