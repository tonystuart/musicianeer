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

  public static class Action {

    private Integer setShift;
    private Integer clearShift;
    private Integer setMode;
    private Integer clearMode;
    private LinkedList<ChannelMessage> sendDeviceMessages;
    private LinkedList<ChannelMessage> sendHandlerMessages;
    private LinkedList<Command> sendHandlerCommands;

    public Integer getClearMode() {
      return clearMode;
    }

    public Integer getClearShift() {
      return clearShift;
    }

    public LinkedList<ChannelMessage> getSendDeviceMessages() {
      return sendDeviceMessages;
    }

    public LinkedList<Command> getSendHandlerCommands() {
      return sendHandlerCommands;
    }

    public LinkedList<ChannelMessage> getSendHandlerMessages() {
      return sendHandlerMessages;
    }

    public Integer getSetMode() {
      return setMode;
    }

    public Integer getSetShift() {
      return setShift;
    }

    public void setClearMode(Integer clearMode) {
      this.clearMode = clearMode;
    }

    public void setClearShift(Integer clearShift) {
      this.clearShift = clearShift;
    }

    public void setSendDeviceMessages(LinkedList<ChannelMessage> sendDeviceMessages) {
      this.sendDeviceMessages = sendDeviceMessages;
    }

    public void setSendHandlerCommands(LinkedList<Command> sendHandlerCommands) {
      this.sendHandlerCommands = sendHandlerCommands;
    }

    public void setSendHandlerMessages(LinkedList<ChannelMessage> sendHandlerMessages) {
      this.sendHandlerMessages = sendHandlerMessages;
    }

    public void setSetMode(Integer setMode) {
      this.setMode = setMode;
    }

    public void setSetShift(Integer setShift) {
      this.setShift = setShift;
    }

    @Override
    public String toString() {
      return "Action [setShift=" + setShift + ", clearShift=" + clearShift + ", setMode=" + setMode + ", clearMode=" + clearMode + ", sendDeviceMessages=" + sendDeviceMessages + ", sendHandlerMessages=" + sendHandlerMessages + ", sendHandlerCommands=" + sendHandlerCommands + "]";
    }

  }

  public static class ChannelMessage {

    private Integer subDevice;
    private Integer command;
    private Integer channel;
    private Integer data1;
    private Integer data2;

    public boolean equals(int subDevice, int command, int channel, int data1, int data2) {
      return (this.subDevice == null || this.subDevice == subDevice) && //
          (this.command == null || this.command == command) && //
          (this.channel == null || this.channel == channel) && //
          (this.data1 == null || this.data1 == data1) && //
          (this.data2 == null || this.data2 == data2);
    }

    public Integer getChannel() {
      return channel;
    }

    public Integer getCommand() {
      return command;
    }

    public Integer getData1() {
      return data1;
    }

    public Integer getData2() {
      return data2;
    }

    public Integer getSubDevice() {
      return subDevice;
    }

    public void setChannel(Integer channel) {
      this.channel = channel;
    }

    public void setCommand(Integer command) {
      this.command = command;
    }

    public void setData1(Integer data1) {
      this.data1 = data1;
    }

    public void setData2(Integer data2) {
      this.data2 = data2;
    }

    public void setSubDevice(Integer subdevice) {
      this.subDevice = subdevice;
    }

    @Override
    public String toString() {
      return String.format("Output [command=%d (0x%02x), channel=%d (0x%02x), data1=%d (0x%02x), data2=%d (0x%02x)", command, command, channel, channel, data1, data1, data2, data2);
    }

  }

  public static class Command {

    private Integer command;
    private Integer parameter;

    public Integer getCommand() {
      return command;
    }

    public Integer getParameter() {
      return parameter;
    }

    public void setCommand(Integer command) {
      this.command = command;
    }

    public void setParameter(Integer parameter) {
      this.parameter = parameter;
    }

    @Override
    public String toString() {
      return "Command [command=" + command + ", parameter=" + parameter + "]";
    }

  }

  public static class Input {

    private ChannelMessage ifMatch;
    private Integer andIfMode;
    private Integer andIfShift;
    private Action onPress;
    private Action onRelease;

    public boolean equals(int subdevice, int command, int channel, int data1, int data2) {
      return ifMatch.equals(subdevice, command, channel, data1, data2);
    }

    public Integer getAndIfMode() {
      return andIfMode;
    }

    public Integer getAndIfShift() {
      return andIfShift;
    }

    public ChannelMessage getIfMatch() {
      return ifMatch;
    }

    public Action getOnPress() {
      return onPress;
    }

    public Action getOnRelease() {
      return onRelease;
    }

    public void setAndIfMode(Integer andIfMode) {
      this.andIfMode = andIfMode;
    }

    public void setAndIfShift(Integer andIfShift) {
      this.andIfShift = andIfShift;
    }

    public void setIfMatch(ChannelMessage ifMatch) {
      this.ifMatch = ifMatch;
    }

    public void setOnPress(Action onPress) {
      this.onPress = onPress;
    }

    public void setOnRelease(Action onRelease) {
      this.onRelease = onRelease;
    }

    @Override
    public String toString() {
      return "Input [ifMatch=" + ifMatch + ", andIfMode=" + andIfMode + ", andIfShift=" + andIfShift + ", onPress=" + onPress + ", onRelease=" + onRelease + "]";
    }

  }

  private LinkedList<ChannelMessage> initializers;
  private LinkedList<Input> inputs;

  public LinkedList<ChannelMessage> getInitializers() {
    return initializers;
  }

  public LinkedList<Input> getInputs() {
    return inputs;
  }

  public void setInitializers(LinkedList<ChannelMessage> initializers) {
    this.initializers = initializers;
  }

  public void setInputs(LinkedList<Input> inputs) {
    this.inputs = inputs;
  }

  @Override
  public String toString() {
    return "MidiConfiguration [initializers=" + initializers + ", inputs=" + inputs + "]";
  }

}
