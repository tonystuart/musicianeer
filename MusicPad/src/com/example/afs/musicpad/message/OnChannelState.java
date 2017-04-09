// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.device.midi.configuration.ChannelState;

public class OnChannelState implements Message {

  private int channel;
  private ChannelState channelState;

  public OnChannelState(int channel, ChannelState active) {
    this.channel = channel;
    this.channelState = active;
  }

  public int getChannel() {
    return channel;
  }

  public ChannelState getChannelState() {
    return channelState;
  }

  @Override
  public String toString() {
    return "OnChannelState [channel=" + channel + ", channelState=" + channelState + "]";
  }

}
