// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.message.TypedMessage;

public class OnNoteOff extends TypedMessage {

  private int data1;

  public OnNoteOff(int data1) {
    this.data1 = data1;
  }

  public int getData1() {
    return data1;
  }

  @Override
  public String toString() {
    return "OnNoteOff [data1=" + data1 + "]";
  }

}
