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

public class OnMute implements Message {

  private int channel;
  private boolean isMute;

  public OnMute(int channel, boolean isMute) {
    this.channel = channel;
    this.isMute = isMute;
  }

  public int getChannel() {
    return channel;
  }

  public boolean isMute() {
    return isMute;
  }

  @Override
  public String toString() {
    return "OnMute [channel=" + channel + ", isMute=" + isMute + "]";
  }

}
