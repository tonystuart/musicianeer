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

public class OnSetChannelVolume implements Message {

  private int channel;
  private int volume;

  public OnSetChannelVolume(int channel, int volume) {
    this.channel = channel;
    this.volume = volume;
  }

  public int getChannel() {
    return channel;
  }

  public int getVolume() {
    return volume;
  }

  @Override
  public String toString() {
    return "OnSetChannelVolume [channel=" + channel + ", volume=" + volume + "]";
  }

}
