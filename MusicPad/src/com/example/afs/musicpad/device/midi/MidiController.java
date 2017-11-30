// Copyright 2016 Anthony F. Stuart - All rights reserved.
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

import com.example.afs.musicpad.device.common.Configuration;
import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.util.FileUtilities;
import com.example.afs.musicpad.util.JsonUtilities;

public class MidiController implements Controller {

  private MidiReader midiReader;
  private MidiWriter midiWriter;
  private MidiConfiguration configuration;
  private MidiDeviceBundle midiDeviceBundle;

  public MidiController(DeviceHandler deviceHandler, MidiDeviceBundle midiDeviceBundle) {
    this.midiDeviceBundle = midiDeviceBundle;
    //configuration = initializeConfiguration(deviceHandler.getBroker(), deviceHandler.getDeviceIndex());
    MessageBroker broker = deviceHandler.tsGetBroker();
    configuration = new BeatStepConfiguration(broker, deviceHandler.tsGetDeviceIndex());
    midiReader = new MidiReader(broker, deviceHandler, midiDeviceBundle, configuration);
    midiWriter = new MidiWriter(broker, midiDeviceBundle, configuration, deviceHandler.tsGetDeviceIndex());
  }

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public String getDeviceName() {
    return midiDeviceBundle.getType();
  }

  @Override
  public void start() {
    midiReader.start();
    midiWriter.tsStart();
  }

  @Override
  public void terminate() {
    midiReader.terminate();
    midiWriter.tsTerminate();
  }

  private MidiConfiguration initializeConfiguration(MessageBroker broker, int deviceIndex) {
    String home = System.getProperty("user.home");
    String type = midiDeviceBundle.getType();
    String fileName = type + ".configuration";
    String overridePathName = home + File.separatorChar + ".musicpad" + File.separatorChar + fileName;
    File configurationFile = new File(overridePathName);
    if (configurationFile.isFile() && configurationFile.canRead()) {
      String contents = FileUtilities.read(fileName);
      MidiConfiguration configuration = JsonUtilities.fromJson(contents, MidiConfiguration.class);
      return configuration;
    }
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
    if (inputStream != null) {
      String contents = FileUtilities.read(inputStream);
      MidiConfiguration configuration = JsonUtilities.fromJson(contents, MidiConfiguration.class);
      return configuration;
    }
    System.out.println("Cannot find configuration for " + type + ", using default");
    return new MidiConfiguration(broker, deviceIndex);
  }

}