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

public class OnSolo implements Message {

  private int channel;
  private boolean isSolo;

  public OnSolo(int channel, boolean isSolo) {
    this.channel = channel;
    this.isSolo = isSolo;
  }

  public int getChannel() {
    return channel;
  }

  public boolean isSolo() {
    return isSolo;
  }

  @Override
  public String toString() {
    return "OnSolo [channel=" + channel + ", isSolo=" + isSolo + "]";
  }

}
