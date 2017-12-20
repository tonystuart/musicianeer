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
import com.example.afs.musicpad.player.PlayableMap;
import com.example.afs.musicpad.util.DelayTimer;
import com.example.afs.musicpad.util.FileUtilities;
import com.example.afs.musicpad.util.JsonUtilities;

public class MidiConfiguration implements Configuration {

  public enum ChannelState {
    SELECTED, ACTIVE, INACTIVE
  }

  public static class ConfigurationData {
    private String deviceType;
    private NavigableMap<InputMessage, OutputMessage> inputMap = new TreeMap<>();

    public ConfigurationData(String deviceType, NavigableMap<InputMessage, OutputMessage> inputMap) {
      this.deviceType = deviceType;
      this.inputMap = inputMap;
    }
  }

  private static final int WRITE_BEHIND_DELAY_MS = 15000;

  public static MidiConfiguration readConfiguration(String deviceType) {
    String fileName = getConfigurationFilename(deviceType);
    File configurationFile = getOverrideFile(fileName);
    if (configurationFile.isFile() && configurationFile.canRead()) {
      String contents = FileUtilities.read(configurationFile);
      ConfigurationData configurationData = JsonUtilities.fromJson(contents, ConfigurationData.class);
      return new MidiConfiguration(configurationData);
    }
    InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(fileName);
    if (inputStream != null) {
      String contents = FileUtilities.read(inputStream);
      ConfigurationData configurationData = JsonUtilities.fromJson(contents, ConfigurationData.class);
      return new MidiConfiguration(configurationData);
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
  private NavigableMap<InputMessage, OutputMessage> inputMap;

  private InputMap groupInputMap;
  private InputMap soundInputMap;
  private DelayTimer delayTimer = new DelayTimer(() -> writeConfiguration());

  public MidiConfiguration(ConfigurationData configurationData) {
    this.deviceType = configurationData.deviceType;
    this.inputMap = configurationData.inputMap;
  }

  public MidiConfiguration(String deviceType) {
    this.deviceType = deviceType;
    this.inputMap = new TreeMap<>();
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
      TreeMap<Integer, String> map = getMap(OutputType.KARAOKE_SELECT_GROUP);
      map.put(PlayableMap.DEFAULT_GROUP, "");
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
      soundInputMap = new InputMap(getMap(OutputType.KARAOKE_SELECT_SOUND));
    }
    return soundInputMap;
  }

  public void put(InputMessage inputMessage, OutputMessage outputMessage) {
    inputMap.put(inputMessage, outputMessage);
    applyChanges();
  }

  public void remove(InputMessage inputMessage) {
    inputMap.remove(inputMessage);
    applyChanges();
  }

  @Override
  public String toString() {
    return "MidiConfiguration [deviceType=" + deviceType + ", inputMap=" + inputMap + "]";
  }

  private void applyChanges() {
    groupInputMap = null;
    soundInputMap = null;
    delayTimer.delay(WRITE_BEHIND_DELAY_MS);
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
    JsonUtilities.toJsonFile(configurationFile, new ConfigurationData(deviceType, inputMap));
    System.out.println("Updating configuration for " + deviceType);
  }

}
