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

public class OnControlChange implements Message {

  private int control;
  private int value;

  public OnControlChange(int control, int value) {
    this.control = control;
    this.value = value;
  }

  public int getControl() {
    return control;
  }

  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "OnControlChange [control=" + control + ", value=" + value + "]";
  }

}
