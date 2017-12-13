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

import com.example.afs.musicpad.device.common.Configuration;
import com.example.afs.musicpad.device.common.InputMap;
import com.example.afs.musicpad.util.FileUtilities;
import com.example.afs.musicpad.util.JsonUtilities;

public class MidiConfiguration implements Configuration {

  public enum ChannelState {
    SELECTED, ACTIVE, INACTIVE
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

  public OutputMessage get(InputMessage inputMessage) {
    return inputMap.get(inputMessage);
  }

  public String getDeviceType() {
    return deviceType;
  }

  @Override
  public InputMap getGroupInputMap() {
    if (groupInputMap == null) {
      groupInputMap = new InputMap(getMap(OutputType.KARAOKE_SELECT_GROUP));
    }
    return groupInputMap;
  }

  public NavigableMap<InputMessage, OutputMessage> getInputMap() {
    return inputMap;
  }

  @Override
  public InputMap getSoundInputMap() {
    if (soundInputMap == null) {
      soundInputMap = new InputMap(getMap(OutputType.KARAOKE_SELECT_SOUND));
    }
    return soundInputMap;
  }

  public void put(InputMessage inputMessage, OutputMessage outputMessage) {
    groupInputMap = null;
    soundInputMap = null;
    inputMap.put(inputMessage, outputMessage);
    writeConfiguration();
  }

  @Override
  public String toString() {
    return "MidiConfiguration [deviceType=" + deviceType + ", inputMap=" + inputMap + "]";
  }

  private TreeMap<Integer, String> getMap(OutputType desiredOutputType) {
    TreeMap<Integer, String> map = new TreeMap<>();
    for (OutputMessage outputMessage : inputMap.values()) {
      OutputType outputType = outputMessage.getOutputType();
      if (outputType == desiredOutputType) {
        map.put(outputMessage.getIndex(), outputMessage.getLabel());
      }
    }
    return map;
  }

  private void writeConfiguration() {
    String fileName = getConfigurationFilename(deviceType);
    File configurationFile = getOverrideFile(fileName);
    configurationFile.getParentFile().mkdirs();
    JsonUtilities.toJsonFile(configurationFile, this);
    System.out.println("Updating configuration for " + deviceType);
  }

}
