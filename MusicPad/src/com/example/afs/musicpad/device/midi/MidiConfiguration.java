// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import java.util.LinkedList;

public class MidiConfiguration {

  public static class ConfigurationMessage {
    private int command;
    private int channel;
    private int data1;
    private int data2;

    public int getChannel() {
      return channel;
    }

    public int getCommand() {
      return command;
    }

    public int getData1() {
      return data1;
    }

    public int getData2() {
      return data2;
    }

    public void setChannel(int channel) {
      this.channel = channel;
    }

    public void setCommand(int command) {
      this.command = command;
    }

    public void setData1(int data1) {
      this.data1 = data1;
    }

    public void setData2(int data2) {
      this.data2 = data2;
    }

    @Override
    public String toString() {
      return String.format("ConfigurationMessage [command=%d (0x%02x), channel=%d (0x%02x), data1=%d (0x%02x), data2=%d (0x%02x)", command, command, channel, channel, data1, data1, data2, data2);
    }

  }

  private LinkedList<ConfigurationMessage> initializers;

  public LinkedList<ConfigurationMessage> getInitializers() {
    return initializers;
  }

  public void setInitializers(LinkedList<ConfigurationMessage> initializers) {
    this.initializers = initializers;
  }

  @Override
  public String toString() {
    return "MidiConfiguration [initializers=" + initializers + "]";
  }

}
