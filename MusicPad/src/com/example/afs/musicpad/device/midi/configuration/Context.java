// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class Context {

  private int port;
  private int command;
  private int channel;
  private int data1;
  private int data2;
  private ChannelState channelState;
  private ConfigurationSupport configurationSupport;

  public Context(ConfigurationSupport configurationSupport) {
    this.configurationSupport = configurationSupport;
  }

  public Context(ConfigurationSupport configurationSupport, int channel, ChannelState channelState) {
    this.configurationSupport = configurationSupport;
    this.channel = channel;
    this.channelState = channelState;
  }

  public Context(ConfigurationSupport configurationSupport, int port, int command, int channel, int data1, int data2) {
    this.configurationSupport = configurationSupport;
    this.port = port;
    this.command = command;
    this.channel = channel;
    this.data1 = data1;
    this.data2 = data2;
  }

  public int getChannel() {
    return channel;
  }

  public ChannelState getChannelState() {
    return channelState;
  }

  public int getCommand() {
    return command;
  }

  public ConfigurationSupport getConfigurationSupport() {
    return configurationSupport;
  }

  public int getData1() {
    return data1;
  }

  public int getData2() {
    return data2;
  }

  public int getPort() {
    return port;
  }

  public boolean isTrace() {
    return true;
  }

  @Override
  public String toString() {
    return "Context [port=" + port + ", command=" + command + ", channel=" + channel + ", data1=" + data1 + ", data2=" + data2 + ", channelState=" + channelState + "]";
  }

}