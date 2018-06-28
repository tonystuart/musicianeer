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

public class OnNoteOn implements Message {

  private int channel;
  private int data1;
  private int data2;

  public OnNoteOn(int channel, int data1, int data2) {
    this.channel = channel;
    this.data1 = data1;
    this.data2 = data2;
  }

  public int getChannel() {
    return channel;
  }

  public int getData1() {
    return data1;
  }

  public int getData2() {
    return data2;
  }

  @Override
  public String toString() {
    return "OnNoteOn [channel=" + channel + ", data1=" + data1 + ", data2=" + data2 + "]";
  }

}
