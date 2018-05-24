// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.task.Message;

public class OnChannelPressure implements Message {

  private int channel;
  private int pressure;

  public OnChannelPressure(int channel, int pressure) {
    this.channel = channel;
    this.pressure = pressure;
  }

  public int getChannel() {
    return channel;
  }

  public int getPressure() {
    return pressure;
  }

  @Override
  public String toString() {
    return "OnChannelPressure [channel=" + channel + ", pressure=" + pressure + "]";
  }

}
