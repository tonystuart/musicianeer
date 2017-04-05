// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import java.util.List;

public class MidiConfiguration {

  public static class Action {

    private Integer setMode;
    private Integer clearMode;
    private List<ChannelMessage> sendDeviceMessages;
    private List<ChannelMessage> sendHandlerMessages;
    private List<Command> sendHandlerCommands;

    public Integer getClearMode() {
      return clearMode;
    }

    public List<ChannelMessage> getSendDeviceMessages() {
      return sendDeviceMessages;
    }

    public List<Command> getSendHandlerCommands() {
      return sendHandlerCommands;
    }

    public List<ChannelMessage> getSendHandlerMessages() {
      return sendHandlerMessages;
    }

    public Integer getSetMode() {
      return setMode;
    }

    public void setClearMode(Integer clearMode) {
      this.clearMode = clearMode;
    }

    public void setSendDeviceMessages(List<ChannelMessage> sendDeviceMessages) {
      this.sendDeviceMessages = sendDeviceMessages;
    }

    public void setSendHandlerCommands(List<Command> sendHandlerCommands) {
      this.sendHandlerCommands = sendHandlerCommands;
    }

    public void setSendHandlerMessages(List<ChannelMessage> sendHandlerMessages) {
      this.sendHandlerMessages = sendHandlerMessages;
    }

    public void setSetMode(Integer setMode) {
      this.setMode = setMode;
    }

    @Override
    public String toString() {
      return "Action [setMode=" + setMode + ", clearMode=" + clearMode + ", sendDeviceMessages=" + sendDeviceMessages + ", sendHandlerMessages=" + sendHandlerMessages + ", sendHandlerCommands=" + sendHandlerCommands + "]";
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
      return String.format("ChannelMessage [command=%d (0x%02x), channel=%d (0x%02x), data1=%d (0x%02x), data2=%d (0x%02x)", command, command, channel, channel, data1, data1, data2, data2);
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

  public static class InputActions {

    private ChannelMessage ifMatch;
    private List<Integer> ifModes;
    private List<Integer> ifNotModes;
    private List<Action> actions;

    public boolean equals(int subdevice, int command, int channel, int data1, int data2) {
      return ifMatch.equals(subdevice, command, channel, data1, data2);
    }

    public List<Action> getActions() {
      return actions;
    }

    public ChannelMessage getIfMatch() {
      return ifMatch;
    }

    public List<Integer> getIfModes() {
      return ifModes;
    }

    public List<Integer> getIfNotModes() {
      return ifNotModes;
    }

    public void setActions(List<Action> actions) {
      this.actions = actions;
    }

    public void setIfMatch(ChannelMessage ifMatch) {
      this.ifMatch = ifMatch;
    }

    public void setIfModes(List<Integer> andIfModes) {
      this.ifModes = andIfModes;
    }

    public void setIfNotModes(List<Integer> ifNotModes) {
      this.ifNotModes = ifNotModes;
    }

    @Override
    public String toString() {
      return "InputActions [ifMatch=" + ifMatch + ", ifModes=" + ifModes + ", ifNotModes=" + ifNotModes + ", actions=" + actions + "]";
    }

  }

  private List<Action> initializationActions;
  private List<InputActions> inputActions;

  public List<Action> getInitializationActions() {
    return initializationActions;
  }

  public List<InputActions> getInputActions() {
    return inputActions;
  }

  public void setInitializationActions(List<Action> initializationActions) {
    this.initializationActions = initializationActions;
  }

  public void setInputActions(List<InputActions> inputActions) {
    this.inputActions = inputActions;
  }

  @Override
  public String toString() {
    return "MidiConfiguration [initializationActions=" + initializationActions + ", inputActions=" + inputActions + "]";
  }

}
