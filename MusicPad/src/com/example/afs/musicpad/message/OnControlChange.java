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

public class OnControlChange implements Message {

  private int channel;
  private int control;
  private int value;

  public OnControlChange(int channel, int control, int value) {
    this.channel = channel;
    this.control = control;
    this.value = value;
  }

  public int getChannel() {
    return channel;
  }

  public int getControl() {
    return control;
  }

  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "OnControlChange [channel=" + channel + ", control=" + control + ", value=" + value + "]";
  }

}
