// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import javax.sound.midi.ShortMessage;

public class OnShortMessage extends TypedMessage {

  private int deviceIndex;
  private ShortMessage shortMessage;

  public OnShortMessage(int deviceIndex, ShortMessage shortMessage) {
    this.deviceIndex = deviceIndex;
    this.shortMessage = shortMessage;
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  public ShortMessage getShortMessage() {
    return shortMessage;
  }

  @Override
  public String toString() {
    return "OnShortMessage [deviceIndex=" + deviceIndex + ", shortMessage=" + shortMessage + "]";
  }

}
