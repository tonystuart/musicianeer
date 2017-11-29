// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.task.Message;

public class OnInputMessage implements Message {

  public enum MessageType {
    DOWN, UP, CONTROL_CHANGE
  }

  private MessageType messageType;
  private int deviceIndex;
  private int data1;

  private int data2;

  public OnInputMessage(MessageType messageType, int deviceIndex, int data1, int data2) {
    this.messageType = messageType;
    this.deviceIndex = deviceIndex;
    this.data1 = data1;
    this.data2 = data2;
  }

  public int getData1() {
    return data1;
  }

  public int getData2() {
    return data2;
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  public MessageType getMessageType() {
    return messageType;
  }

  @Override
  public String toString() {
    return "OnInputMessage [messageType=" + messageType + ", deviceIndex=" + deviceIndex + ", data1=" + data1 + ", data2=" + data2 + "]";
  }

}
