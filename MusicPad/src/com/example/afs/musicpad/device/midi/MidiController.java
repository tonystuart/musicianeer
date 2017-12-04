// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import com.example.afs.musicpad.device.common.Configuration;
import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.task.MessageBroker;

public class MidiController implements Controller {

  private MidiReader midiReader;
  private MidiWriter midiWriter;
  private MidiConfiguration configuration;
  private MidiDeviceBundle midiDeviceBundle;

  public MidiController(DeviceHandler deviceHandler, MidiDeviceBundle midiDeviceBundle) {
    this.midiDeviceBundle = midiDeviceBundle;
    MessageBroker broker = deviceHandler.tsGetBroker();
    configuration = MidiConfiguration.readConfiguration(midiDeviceBundle.getType());
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

}