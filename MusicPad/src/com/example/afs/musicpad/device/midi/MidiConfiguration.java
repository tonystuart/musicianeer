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

  public static class GroupLabelledIndex extends LabelledIndex {
    public GroupLabelledIndex(String label, int index) {
      super(label, index);
    }
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

  public static class LabelledIndex {
    private String label;
    private int index;

    public LabelledIndex(String label, int index) {
      this.label = label;
      this.index = index;
    }

    public int getIndex() {
      return index;
    }

    public String getLabel() {
      return label;
    }

    @Override
    public String toString() {
      return "LabelledIndex [label=" + label + ", index=" + index + "]";
    }

  }

  public static class OutputMessage {
    private Command command;
    private DeviceCommand deviceCommand;
    private GroupLabelledIndex group;
    private SoundLabelledIndex sound;

    public OutputMessage(Command command) {
      this.command = command;
    }

    public OutputMessage(Command command, DeviceCommand deviceCommand, GroupLabelledIndex group, SoundLabelledIndex sound) {
      this.command = command;
      this.deviceCommand = deviceCommand;
      this.group = group;
      this.sound = sound;
    }

    public OutputMessage(DeviceCommand deviceCommand) {
      this.deviceCommand = deviceCommand;
    }

    public OutputMessage(GroupLabelledIndex group) {
      this.group = group;
    }

    public OutputMessage(SoundLabelledIndex sound) {
      this.sound = sound;
    }

    public Command getCommand() {
      return command;
    }

    public DeviceCommand getDeviceCommand() {
      return deviceCommand;
    }

    public GroupLabelledIndex getGroup() {
      return group;
    }

    public SoundLabelledIndex getSound() {
      return sound;
    }

  }

  public static class SoundLabelledIndex extends LabelledIndex {
    public SoundLabelledIndex(String label, int index) {
      super(label, index);
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
    return inputMap.get(new InputMessage(shortMessage));
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void put(ShortMessage shortMessage, Command command) {
    put(shortMessage, new OutputMessage(command));
  }

  public void put(ShortMessage shortMessage, DeviceCommand deviceCommand) {
    put(shortMessage, new OutputMessage(deviceCommand));
  }

  public void put(ShortMessage shortMessage, GroupLabelledIndex groupLabelledIndex) {
    put(shortMessage, new OutputMessage(groupLabelledIndex));
    writeConfiguration();
  }

  public void put(ShortMessage shortMessage, SoundLabelledIndex soundLabelledIndex) {
    put(shortMessage, new OutputMessage(soundLabelledIndex));
    writeConfiguration();
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
