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

import com.example.afs.musicpad.device.common.DeviceBundle;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.device.midi.configuration.MidiConfiguration;
import com.example.afs.musicpad.device.midi.configuration.Parser;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.FileUtilities;

public class MidiController implements Controller {

  private MidiReader midiReader;
  private MidiWriter midiWriter;
  private DeviceHandler deviceHandler;
  private MidiDeviceBundle midiDeviceBundle;

  public MidiController(DeviceHandler deviceHandler, DeviceBundle deviceBundle) {
    this.deviceHandler = deviceHandler;
    this.midiDeviceBundle = (MidiDeviceBundle) deviceBundle;
    MidiConfiguration configuration = initializeConfiguration();
    deviceHandler.setInputMapping(new MidiMapping());
    Broker<Message> broker = deviceHandler.getBroker();
    midiReader = new MidiReader(broker, deviceHandler.getInputQueue(), midiDeviceBundle, configuration);
    midiWriter = new MidiWriter(broker, midiDeviceBundle, configuration);
  }

  @Override
  public int getDevice() {
    return deviceHandler.getDeviceIndex();
  }

  @Override
  public void start() {
    deviceHandler.start();
    midiReader.start();
    midiWriter.start();
  }

  @Override
  public void terminate() {
    deviceHandler.terminate();
    midiReader.terminate();
    midiWriter.terminate();
  }

  private MidiConfiguration initializeConfiguration() {
    String home = System.getProperty("user.home");
    String type = midiDeviceBundle.getType();
    String fileName = type + ".configuration";
    String overridePathName = home + File.separatorChar + ".musicpad" + File.separatorChar + fileName;
    File configurationFile = new File(overridePathName);
    if (configurationFile.isFile() && configurationFile.canRead()) {
      String contents = FileUtilities.read(fileName);
      MidiConfiguration configuration = new Parser().parse(contents);
      return configuration;
    }
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
    if (inputStream != null) {
      String contents = FileUtilities.read(inputStream);
      MidiConfiguration configuration = new Parser().parse(contents);
      return configuration;
    }
    System.out.println("Cannot find configuration for " + type + ", using default");
    return new MidiConfiguration();
  }

}