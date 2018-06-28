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

public class OnNoteOff implements Message {

  private int channel;
  private int data1;

  public OnNoteOff(int channel, int data1) {
    this.channel = channel;
    this.data1 = data1;
  }

  public int getChannel() {
    return channel;
  }

  public int getData1() {
    return data1;
  }

  @Override
  public String toString() {
    return "OnNoteOff [channel=" + channel + ", data1=" + data1 + "]";
  }

}
