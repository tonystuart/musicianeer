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

public class OnPitchBend implements Message {

  private int channel;
  private int value;

  public OnPitchBend(int channel, int value) {
    this.channel = channel;
    this.value = value;
  }

  public int getChannel() {
    return channel;
  }

  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "OnPitchBend [channel=" + channel + ", value=" + value + "]";
  }

}
