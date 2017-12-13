// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import javax.sound.midi.ShortMessage;

public class InputMessage implements Comparable<InputMessage> {

  private int channel;
  private int control;
  private boolean isKey;

  public InputMessage(ShortMessage shortMessage) {
    int command = shortMessage.getCommand();
    channel = shortMessage.getChannel();
    control = shortMessage.getData1();
    switch (command) {
    case ShortMessage.NOTE_OFF:
    case ShortMessage.NOTE_ON:
    case ShortMessage.POLY_PRESSURE:
    case ShortMessage.CHANNEL_PRESSURE:
      isKey = true;
      break;
    default:
      isKey = false;
      break;
    }
  }

  @Override
  public int compareTo(InputMessage that) {
    int relation = this.channel - that.channel;
    if (relation != 0) {
      return relation;
    }
    relation = this.control - that.control;
    if (relation != 0) {
      return relation;
    }
    relation = Boolean.compare(this.isKey, that.isKey);
    return relation;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    InputMessage other = (InputMessage) obj;
    if (channel != other.channel) {
      return false;
    }
    if (control != other.control) {
      return false;
    }
    if (isKey != other.isKey) {
      return false;
    }
    return true;
  }

  public int getChannel() {
    return channel;
  }

  public int getControl() {
    return control;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + channel;
    result = prime * result + control;
    result = prime * result + (isKey ? 1231 : 1237);
    return result;
  }

  public boolean isKey() {
    return isKey;
  }

  @Override
  public String toString() {
    return "InputMessage [channel=" + channel + ", control=" + control + ", isKey=" + isKey + "]";
  }

}