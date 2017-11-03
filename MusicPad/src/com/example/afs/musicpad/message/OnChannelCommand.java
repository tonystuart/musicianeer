// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.ChannelCommand;

public class OnChannelCommand extends TypedMessage {

  private ChannelCommand channelCommand;
  private int channel;
  private int parameter;

  public OnChannelCommand() {
  }

  public OnChannelCommand(ChannelCommand channelCommand, int channel, int parameter) {
    this.channelCommand = channelCommand;
    this.channel = channel;
    this.parameter = parameter;
  }

  public int getChannel() {
    return channel;
  }

  public ChannelCommand getChannelCommand() {
    return channelCommand;
  }

  public int getParameter() {
    return parameter;
  }

  public void setChannel(int channel) {
    this.channel = channel;
  }

  public void setChannelCommand(ChannelCommand channelCommand) {
    this.channelCommand = channelCommand;
  }

  public void setParameter(int parameter) {
    this.parameter = parameter;
  }

  @Override
  public String toString() {
    return "OnChannelCommand [channelCommand=" + channelCommand + ", channel=" + channel + ", parameter=" + parameter + "]";
  }

}
