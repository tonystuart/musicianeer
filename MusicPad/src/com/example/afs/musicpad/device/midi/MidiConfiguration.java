// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.device.common.Configuration;
import com.example.afs.musicpad.util.FileUtilities;
import com.example.afs.musicpad.util.JsonUtilities;

public class MidiConfiguration extends Configuration {

  public enum ChannelState {
    SELECTED, ACTIVE, INACTIVE
  }

  public static class InputMessage {

    private int command;
    private int channel;
    private int control;

    public InputMessage(ShortMessage shortMessage) {
      command = shortMessage.getCommand();
      channel = shortMessage.getChannel();
      control = shortMessage.getData1();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      InputMessage other = (InputMessage) obj;
      if (channel != other.channel) {
        return false;
      }
      if (command != other.command) {
        return false;
      }
      if (control != other.control) {
        return false;
      }
      return true;
    }

    public int getChannel() {
      return channel;
    }

    public int getCommand() {
      return command;
    }

    public int getControl() {
      return control;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + channel;
      result = prime * result + command;
      result = prime * result + control;
      return result;
    }

    @Override
    public String toString() {
      return "InputMessage [command=" + command + ", channel=" + channel + ", control=" + control + "]";
    }

  }

  public static class OutputMessage {
    private Command command;
    private DeviceCommand deviceCommand;
    private Integer groupIndex;
    private String groupLabel;

    public OutputMessage() {
    }

    public OutputMessage(Command command) {
      this.command = command;
    }

    public OutputMessage(DeviceCommand deviceCommand) {
      this.deviceCommand = deviceCommand;
    }

    public Integer getGroupIndex() {
      return groupIndex;
    }

    public String getGroupLabel() {
      return groupLabel;
    }

    public Object getValue() {
      Object value;
      if (command != null) {
        value = command;
      } else if (deviceCommand != null) {
        value = deviceCommand;
      } else {
        throw new IllegalStateException();
      }
      return value;
    }

    public void setGroupIndex(Integer groupIndex) {
      this.groupIndex = groupIndex;
    }

    public void setGroupLabel(String groupLabel) {
      this.groupLabel = groupLabel;
    }

    @Override
    public String toString() {
      return "OutputMessage [command=" + command + ", deviceCommand=" + deviceCommand + "]";
    }
  }

  public static MidiConfiguration readConfiguration(String deviceType) {
    String fileName = getConfigurationFilename(deviceType);
    File configurationFile = getOverrideFile(fileName);
    if (configurationFile.isFile() && configurationFile.canRead()) {
      String contents = FileUtilities.read(configurationFile);
      MidiConfiguration configuration = JsonUtilities.fromJson(contents, MidiConfiguration.class);
      return configuration;
    }
    InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(fileName);
    if (inputStream != null) {
      String contents = FileUtilities.read(inputStream);
      MidiConfiguration configuration = JsonUtilities.fromJson(contents, MidiConfiguration.class);
      return configuration;
    }
    System.out.println("Cannot find configuration for " + deviceType + ", using default");
    return new MidiConfiguration(deviceType);
  }

  private static String getConfigurationFilename(String type) {
    return type + ".v1";
  }

  private static File getOverrideFile(String fileName) {
    String home = System.getProperty("user.home");
    String overridePathName = home + File.separatorChar + ".musicpad" + File.separatorChar + fileName;
    File configurationFile = new File(overridePathName);
    return configurationFile;
  }

  private String deviceType;
  private Map<InputMessage, OutputMessage> inputMap = new HashMap<>();

  public MidiConfiguration(String deviceType) {
    this.deviceType = deviceType;
  }

  public Object get(ShortMessage shortMessage) {
    OutputMessage outputMessage = inputMap.get(new InputMessage(shortMessage));
    return outputMessage == null ? null : outputMessage.getValue();
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void put(ShortMessage shortMessage, Command command) {
    inputMap.put(new InputMessage(shortMessage), new OutputMessage(command));
    writeConfiguration();
  }

  public void put(ShortMessage shortMessage, DeviceCommand deviceCommand) {
    inputMap.put(new InputMessage(shortMessage), new OutputMessage(deviceCommand));
    writeConfiguration();
  }

  public void putGroupIndex(ShortMessage shortMessage, int groupIndex) {
    realizeOutputMessage(shortMessage).setGroupIndex(groupIndex);
    writeConfiguration();
  }

  public void putGroupLabel(ShortMessage shortMessage, String groupLabel) {
    realizeOutputMessage(shortMessage).setGroupLabel(groupLabel);
    writeConfiguration();
  }

  public void writeConfiguration() {
    String fileName = getConfigurationFilename(deviceType);
    File configurationFile = getOverrideFile(fileName);
    configurationFile.getParentFile().mkdirs();
    JsonUtilities.toJsonFile(configurationFile, this);
    System.out.println("Updating configuration for " + deviceType);
  }

  private OutputMessage realizeOutputMessage(ShortMessage shortMessage) {
    InputMessage key = new InputMessage(shortMessage);
    OutputMessage outputMessage = inputMap.get(key);
    if (outputMessage == null) {
      outputMessage = new OutputMessage();
      inputMap.put(key, outputMessage);
    }
    return outputMessage;
  }

}
