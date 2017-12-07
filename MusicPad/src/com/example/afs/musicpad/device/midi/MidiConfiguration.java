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
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.device.common.Configuration;
import com.example.afs.musicpad.device.common.InputMap;
import com.example.afs.musicpad.util.FileUtilities;
import com.example.afs.musicpad.util.JsonUtilities;

public class MidiConfiguration implements Configuration {

  public enum ChannelState {
    SELECTED, ACTIVE, INACTIVE
  }

  public static class GroupInputCode extends InputCode {
    public GroupInputCode(String label, int inputCode) {
      super(label, inputCode);
    }
  }

  public static class InputCode {
    private String label;
    private int inputCode;

    public InputCode(String label, int inputCode) {
      this.label = label;
      this.inputCode = inputCode;
    }

    public String asString() {
      return inputCode + " (" + label + ")";
    }

    public int getInputCode() {
      return inputCode;
    }

    public String getLabel() {
      return label;
    }

    @Override
    public String toString() {
      return "LabelledIndex [label=" + label + ", inputCode=" + inputCode + "]";
    }

  }

  public static class InputMessage implements Comparable<InputMessage> {

    private int command;
    private int channel;
    private int control;

    public InputMessage(ShortMessage shortMessage) {
      command = shortMessage.getCommand();
      channel = shortMessage.getChannel();
      control = shortMessage.getData1();
    }

    @Override
    public int compareTo(InputMessage that) {
      int relation = this.channel - that.channel;
      if (relation != 0) {
        return relation;
      }
      relation = this.control - that.control;
      if (relation != 0) {
        return relation;
      }
      relation = this.command - that.command;
      if (relation != 0) {
        if (this.command == ShortMessage.NOTE_ON && that.command == ShortMessage.NOTE_OFF) {
          relation = -1;
        } else if (this.command == ShortMessage.NOTE_OFF && that.command == ShortMessage.NOTE_ON) {
          relation = +1;
        }
        return relation;
      }
      return 0;
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
    private GroupInputCode group;
    private SoundInputCode sound;

    public OutputMessage(Command command) {
      this.command = command;
    }

    public OutputMessage(Command command, DeviceCommand deviceCommand, GroupInputCode group, SoundInputCode sound) {
      this.command = command;
      this.deviceCommand = deviceCommand;
      this.group = group;
      this.sound = sound;
    }

    public OutputMessage(DeviceCommand deviceCommand) {
      this.deviceCommand = deviceCommand;
    }

    public OutputMessage(GroupInputCode group) {
      this.group = group;
    }

    public OutputMessage(SoundInputCode sound) {
      this.sound = sound;
    }

    public String asString() {
      if (command != null) {
        return command.name();
      }
      if (deviceCommand != null) {
        return deviceCommand.name();
      }
      if (group != null) {
        return "GROUP " + group.asString();
      }
      if (sound != null) {
        return "SOUND " + sound.asString();
      }
      throw new UnsupportedOperationException();
    }

    public Command getCommand() {
      return command;
    }

    public DeviceCommand getDeviceCommand() {
      return deviceCommand;
    }

    public GroupInputCode getGroup() {
      return group;
    }

    public SoundInputCode getSound() {
      return sound;
    }

    @Override
    public String toString() {
      return "OutputMessage [command=" + command + ", deviceCommand=" + deviceCommand + ", group=" + group + ", sound=" + sound + "]";
    }

  }

  public static class SoundInputCode extends InputCode {
    public SoundInputCode(String label, int inputCode) {
      super(label, inputCode);
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
  private transient InputMap groupInputMap;
  private transient InputMap soundInputMap;
  private NavigableMap<InputMessage, OutputMessage> inputMap = new TreeMap<>();

  public MidiConfiguration(String deviceType) {
    this.deviceType = deviceType;
  }

  public OutputMessage get(ShortMessage shortMessage) {
    return inputMap.get(new InputMessage(shortMessage));
  }

  public String getDeviceType() {
    return deviceType;
  }

  @Override
  public InputMap getGroupInputMap() {
    if (groupInputMap == null) {
      TreeMap<Integer, String> map = new TreeMap<>();
      for (OutputMessage outputMessage : inputMap.values()) {
        GroupInputCode group = outputMessage.getGroup();
        if (group != null) {
          map.put(group.getInputCode(), group.getLabel());
        }
      }
      groupInputMap = new InputMap(map);
    }
    return groupInputMap;
  }

  public NavigableMap<InputMessage, OutputMessage> getInputMap() {
    return inputMap;
  }

  @Override
  public InputMap getSoundInputMap() {
    if (soundInputMap == null) {
      TreeMap<Integer, String> map = new TreeMap<>();
      for (OutputMessage outputMessage : inputMap.values()) {
        SoundInputCode sound = outputMessage.getSound();
        if (sound != null) {
          map.put(sound.getInputCode(), sound.getLabel());
        }
      }
      soundInputMap = new InputMap(map);
    }
    return soundInputMap;
  }

  public void put(ShortMessage shortMessage, Command command) {
    put(shortMessage, new OutputMessage(command));
  }

  public void put(ShortMessage shortMessage, DeviceCommand deviceCommand) {
    put(shortMessage, new OutputMessage(deviceCommand));
  }

  public void put(ShortMessage shortMessage, GroupInputCode groupInputCode) {
    groupInputMap = null;
    put(shortMessage, new OutputMessage(groupInputCode));
    writeConfiguration();
  }

  public void put(ShortMessage shortMessage, SoundInputCode soundInputCode) {
    soundInputMap = null;
    put(shortMessage, new OutputMessage(soundInputCode));
    writeConfiguration();
  }

  @Override
  public String toString() {
    return "MidiConfiguration [deviceType=" + deviceType + ", inputMap=" + inputMap + "]";
  }

  public void writeConfiguration() {
    String fileName = getConfigurationFilename(deviceType);
    File configurationFile = getOverrideFile(fileName);
    configurationFile.getParentFile().mkdirs();
    JsonUtilities.toJsonFile(configurationFile, this);
    System.out.println("Updating configuration for " + deviceType);
  }

  private void put(ShortMessage shortMessage, OutputMessage outputMessage) {
    inputMap.put(new InputMessage(shortMessage), outputMessage);
    writeConfiguration();
  }

}
