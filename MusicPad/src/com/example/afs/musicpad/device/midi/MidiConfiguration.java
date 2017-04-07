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

import com.example.afs.musicpad.Command;

public class MidiConfiguration {

  public static class Action {

    private Integer setMode;
    private Integer clearMode;
    private List<ChannelMessage> sendDeviceMessages;
    private List<ChannelMessage> sendHandlerMessages;
    private List<HandlerCommand> sendHandlerCommands;

    public Integer getClearMode() {
      return clearMode;
    }

    public List<ChannelMessage> getSendDeviceMessages() {
      return sendDeviceMessages;
    }

    public List<HandlerCommand> getSendHandlerCommands() {
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

    public void setSendHandlerCommands(List<HandlerCommand> sendHandlerCommands) {
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
      return String.format("ChannelMessage [subDevice=%d, command=%d (0x%02x), channel=%d (0x%02x), data1=%d (0x%02x), data2=%d (0x%02x)", subDevice, command, command, channel, channel, data1, data1, data2, data2);
    }

  }

  public enum ChannelState {
    ACTIVE, INACTIVE, SELECTED
  }

  public class ChannelStatus {

    private Integer channelNumber;
    private ChannelState state;

    public Integer getChannelNumber() {
      return channelNumber;
    }

    public ChannelState getState() {
      return state;
    }

    public void setChannelNumber(Integer channelNumber) {
      this.channelNumber = channelNumber;
    }

    public void setState(ChannelState state) {
      this.state = state;
    }

    @Override
    public String toString() {
      return "ChannelStatus [channelNumber=" + channelNumber + ", state=" + state + "]";
    }

  }

  public static class HandlerCommand {

    private Command command;
    private Integer parameter;

    public Command getCommand() {
      return command;
    }

    public Integer getParameter() {
      return parameter;
    }

    public void setCommand(Command command) {
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

  public static class InputAction {

    private ChannelMessage ifInput;
    private List<Integer> ifModes;
    private List<Integer> ifNotModes;
    private Action thenDo;

    public boolean equals(int subdevice, int command, int channel, int data1, int data2) {
      return ifInput.equals(subdevice, command, channel, data1, data2);
    }

    public ChannelMessage getIfInput() {
      return ifInput;
    }

    public List<Integer> getIfModes() {
      return ifModes;
    }

    public List<Integer> getIfNotModes() {
      return ifNotModes;
    }

    public Action getThenDo() {
      return thenDo;
    }

    public void setIfInput(ChannelMessage ifInput) {
      this.ifInput = ifInput;
    }

    public void setIfModes(List<Integer> andIfModes) {
      this.ifModes = andIfModes;
    }

    public void setIfNotModes(List<Integer> ifNotModes) {
      this.ifNotModes = ifNotModes;
    }

    public void setThenDo(Action thenDo) {
      this.thenDo = thenDo;
    }

    @Override
    public String toString() {
      return "InputAction [ifInput=" + ifInput + ", ifModes=" + ifModes + ", ifNotModes=" + ifNotModes + ", thenDo=" + thenDo + "]";
    }

  }

  public class OutputAction {

    private ChannelStatus ifChannelStatus;
    private List<ChannelMessage> thenSendDeviceMessages;

    public ChannelStatus getIfChannelStatus() {
      return ifChannelStatus;
    }

    public List<ChannelMessage> getThenSendDeviceMessages() {
      return thenSendDeviceMessages;
    }

    public void setIfChannelStatus(ChannelStatus ifChannelStatus) {
      this.ifChannelStatus = ifChannelStatus;
    }

    public void setThenSendDeviceMessages(List<ChannelMessage> thenSendDeviceMessages) {
      this.thenSendDeviceMessages = thenSendDeviceMessages;
    }

    @Override
    public String toString() {
      return "OutputAction [ifChannelStatus=" + ifChannelStatus + ", thenSendDeviceMessages=" + thenSendDeviceMessages + "]";
    }

  }

  private Action initializationActions;
  private List<InputAction> inputActions;
  private List<OutputAction> outputActions;

  public Action getInitializationActions() {
    return initializationActions;
  }

  public List<InputAction> getInputActions() {
    return inputActions;
  }

  public List<OutputAction> getOutputActions() {
    return outputActions;
  }

  public void setInitializationActions(Action initializationActions) {
    this.initializationActions = initializationActions;
  }

  public void setInputActions(List<InputAction> inputActions) {
    this.inputActions = inputActions;
  }

  public void setOutputActions(List<OutputAction> outputActions) {
    this.outputActions = outputActions;
  }

  @Override
  public String toString() {
    return "MidiConfiguration [initializationActions=" + initializationActions + ", inputActions=" + inputActions + ", outputActions=" + outputActions + "]";
  }

}
