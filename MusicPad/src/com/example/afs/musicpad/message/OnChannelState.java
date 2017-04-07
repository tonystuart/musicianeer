// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.device.midi.MidiConfiguration.ChannelState;

public class OnChannelState implements Message {

  private int channelNumber;
  private ChannelState channelState;

  public OnChannelState(int channelNumber, ChannelState active) {
    this.channelNumber = channelNumber;
    this.channelState = active;
  }

  public int getChannelNumber() {
    return channelNumber;
  }

  public ChannelState getChannelState() {
    return channelState;
  }

  @Override
  public String toString() {
    return "OnChannelState [channelNumber=" + channelNumber + ", channelState=" + channelState + "]";
  }

}
