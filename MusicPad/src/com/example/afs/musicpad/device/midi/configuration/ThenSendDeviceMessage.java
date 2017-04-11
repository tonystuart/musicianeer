// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

import java.util.Arrays;

public class ThenSendDeviceMessage extends Then {

  private String[] tokens;

  public ThenSendDeviceMessage(int lineIndex, String[] tokens) {
    super(lineIndex);
    if (tokens.length != 6) {
      throw new IllegalArgumentException(formatMessage("Expected sendDeviceMessage port command channel data1 data2"));
    }
    this.tokens = tokens;
  }

  @Override
  public void executeThen(Context context) {
    int port = (int) context.getRight(tokens[1]);
    int command = (int) context.getRight(tokens[2]);
    int channel = (int) context.getRight(tokens[3]);
    int data1 = (int) context.getRight(tokens[4]);
    int data2 = (int) context.getRight(tokens[5]);
    context.getHasSendDeviceMessage().sendDeviceMessage(port, command, channel, data1, data2);
  }

  @Override
  public String toString() {
    return "ThenSendDeviceMessage [tokens=" + Arrays.toString(tokens) + "]";
  }

}