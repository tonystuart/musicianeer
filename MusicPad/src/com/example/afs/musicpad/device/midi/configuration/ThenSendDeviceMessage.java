// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class ThenSendDeviceMessage extends Then {

  private int port;
  private int command;
  private int channel;
  private int data1;
  private int data2;

  public ThenSendDeviceMessage(int lineIndex, String[] tokens) {
    super(lineIndex);
    try {
      port = Integer.decode(tokens[1]);
      command = Integer.decode(tokens[2]);
      channel = Integer.decode(tokens[3]);
      data1 = Integer.decode(tokens[4]);
      data2 = Integer.decode(tokens[5]);
    } catch (RuntimeException e) {
      displayError("Expected sendDeviceMessage port command channel data1 data2");
    }
  }

  @Override
  public void executeThen(Context context) {
    context.getConfigurationSupport().sendDeviceMessage(port, command, channel, data1, data2);
  }

}