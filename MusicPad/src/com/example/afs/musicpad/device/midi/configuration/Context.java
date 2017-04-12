// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.Trace;

/**
 * Provides a context for processing a MIDI configuration.
 * 
 * Note that both reader and writer threads access this context concurrently.
 * For efficiency, we provide direct access to the common properties, which are
 * protected by JLS guarantees about atomicity and concurrency. For the rest we
 * use a concurrent map.
 */
public class Context {

  public interface HasSendDeviceMessage {
    void sendDeviceMessage(int port, int command, int channel, int data1, int data2);
  }

  public interface HasSendHandlerCommand {
    void sendHandlerCommand(Command command, Integer parameter);
  }

  public interface HasSendHandlerMessage {
    void sendHandlerMessage(int data1);
  }

  private static final String PORT = "port";
  private static final String COMMAND = "command";
  private static final String CHANNEL = "channel";
  private static final String DATA1 = "data1";
  private static final String DATA2 = "data2";
  private static final String STATUS_CHANNEL = "statusChannel";
  private static final String CHANNEL_STATE = "channelState";

  private HasSendDeviceMessage hasSendDeviceMessage;
  private HasSendHandlerCommand hasSendHandlerCommand;
  private HasSendHandlerMessage hasSendHandlerMessage;

  private Integer port;
  private Integer command;
  private Integer channel;
  private Integer data1;
  private Integer data2;
  private ChannelState channelState;
  private Map<String, Object> context = new ConcurrentHashMap<>();
  private int statusChannel;

  public <T extends Enum<T>> T get(Class<T> type, String key) {
    T enumValue = null;
    Object objectValue = get(key);
    if (objectValue == null) {
      T[] enumConstants = type.getEnumConstants();
      for (int i = 0; i < enumConstants.length && enumValue == null; i++) {
        T enumType = enumConstants[i];
        if (enumType.name().equals(key)) {
          enumValue = enumType;
        }
      }
    } else if (objectValue instanceof Integer) {
      enumValue = type.getEnumConstants()[(int) objectValue];
    }
    return enumValue;
  }

  public HasSendDeviceMessage getHasSendDeviceMessage() {
    return hasSendDeviceMessage;
  }

  public HasSendHandlerCommand getHasSendHandlerCommand() {
    return hasSendHandlerCommand;
  }

  public HasSendHandlerMessage getHasSendHandlerMessage() {
    return hasSendHandlerMessage;
  }

  public Object getLeft(String key) {
    return get(key);
  }

  public Object getRight(String value) {
    Object right;
    try {
      right = Integer.decode(value);
    } catch (NumberFormatException e) {
      right = get(value);
    }
    return right;
  }

  public boolean isSet(String key) {
    return contains(key);
  }

  public boolean isTrace() {
    return Trace.isTraceConfiguration();
  }

  public void remove(String key) {
    context.remove(key);
  }

  public void set(String key, Object value) {
    put(key, value);
  }

  public void setChannel(int channel) {
    this.channel = channel;
  }

  public void setChannelState(ChannelState channelState) {
    this.channelState = channelState;
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

  public void setHasSendDeviceMessage(HasSendDeviceMessage hasSendDeviceMessage) {
    this.hasSendDeviceMessage = hasSendDeviceMessage;
  }

  public void setHasSendHandlerCommand(HasSendHandlerCommand hasSendHandlerCommand) {
    this.hasSendHandlerCommand = hasSendHandlerCommand;
  }

  public void setHasSendHandlerMessage(HasSendHandlerMessage hasSendHandlerMessage) {
    this.hasSendHandlerMessage = hasSendHandlerMessage;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setStatusChannel(int channelNumber) {
    statusChannel = channelNumber;
  }

  @Override
  public String toString() {
    return "Context [port=" + port + ", command=" + command + ", channel=" + channel + ", data1=" + data1 + ", data2=" + data2 + ", channelState=" + channelState + ", context=" + context + "]";
  }

  private boolean contains(String key) {
    boolean contains;
    switch (key) {
    case PORT:
      contains = true;
      break;
    case COMMAND:
      contains = true;
      break;
    case CHANNEL:
      contains = true;
      break;
    case DATA1:
      contains = true;
      break;
    case DATA2:
      contains = true;
      break;
    case STATUS_CHANNEL:
      contains = true;
      break;
    case CHANNEL_STATE:
      contains = true;
      break;
    default:
      contains = context.containsKey(key);
      break;
    }
    return contains;
  }

  private Object get(String key) {
    Object value;
    switch (key) {
    case PORT:
      value = port;
      break;
    case COMMAND:
      value = command;
      break;
    case CHANNEL:
      value = channel;
      break;
    case DATA1:
      value = data1;
      break;
    case DATA2:
      value = data2;
      break;
    case STATUS_CHANNEL:
      value = statusChannel;
      break;
    case CHANNEL_STATE:
      value = channelState;
      break;
    default:
      value = context.get(key);
      break;
    }
    return value;
  }

  private void put(String key, Object value) {
    switch (key) {
    case PORT:
      port = (Integer) value;
      break;
    case COMMAND:
      command = (Integer) value;
      break;
    case CHANNEL:
      channel = (Integer) value;
      break;
    case DATA1:
      data1 = (Integer) value;
      break;
    case DATA2:
      data2 = (Integer) value;
      break;
    case STATUS_CHANNEL:
      statusChannel = (Integer) value;
      break;
    case CHANNEL_STATE:
      channelState = (ChannelState) value;
      break;
    default:
      context.put(key, value);
      break;
    }
  }

}