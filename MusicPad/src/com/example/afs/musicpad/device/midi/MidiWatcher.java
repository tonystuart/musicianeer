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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.device.common.ControllableGroup;
import com.example.afs.musicpad.device.common.Device;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.device.midi.configuration.MidiConfiguration;
import com.example.afs.musicpad.device.midi.configuration.Parser;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.player.Player.UnitType;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.FileUtilities;

public class MidiWatcher extends BrokerTask<Message> {

  private static final Pattern PATTERN = Pattern.compile("^(.*) \\[hw\\:([0-9]+),([0-9]+),([0-9]+)\\]$");

  private Synthesizer synthesizer;
  private Map<String, ControllableGroup> oldDevices = new HashMap<>();

  public MidiWatcher(Broker<Message> broker, Synthesizer synthesizer) {
    super(broker, 1000);
    this.synthesizer = synthesizer;
  }

  @Override
  public void onTimeout() throws InterruptedException {
    Map<String, MidiDeviceBundle> newDevices = getDevices();
    Iterator<Entry<String, ControllableGroup>> oldIterator = oldDevices.entrySet().iterator();
    while (oldIterator.hasNext()) {
      Entry<String, ControllableGroup> oldEntry = oldIterator.next();
      if (!newDevices.containsKey(oldEntry.getKey())) {
        detachDevice(oldEntry.getKey(), oldEntry.getValue());
        oldIterator.remove();
      }
    }
    for (Entry<String, MidiDeviceBundle> newEntry : newDevices.entrySet()) {
      if (!oldDevices.containsKey(newEntry.getKey())) {
        attachDevice(newEntry.getKey(), newEntry.getValue());
      }
    }
  }

  private void attachDevice(String name, MidiDeviceBundle deviceBundle) {
    System.out.println("Attaching MIDI device " + name);
    MidiConfiguration configuration = readConfiguration(deviceBundle);
    Device device = new Device(name);
    device.setInputMapping(new MidiMapping());
    device.setUnitType(UnitType.NOTE);
    DeviceHandler deviceHandler = new DeviceHandler(getBroker(), synthesizer, device);
    MidiReader midiReader = new MidiReader(getBroker(), deviceHandler.getInputQueue(), deviceBundle, configuration);
    MidiWriter midiWriter = new MidiWriter(getBroker(), deviceBundle, configuration);
    ControllableGroup controllableGroup = new ControllableGroup(deviceHandler, midiReader, midiWriter);
    oldDevices.put(name, controllableGroup);
    controllableGroup.start();
  }

  private void detachDevice(String name, ControllableGroup controllableGroup) {
    System.out.println("Detaching MIDI device " + name);
    controllableGroup.terminate();
  }

  private Map<String, MidiDeviceBundle> getDevices() {
    try {
      Map<String, MidiDeviceBundle> devices = new HashMap<>();
      Info[] deviceDescriptors = MidiSystem.getMidiDeviceInfo();
      for (Info deviceDescriptor : deviceDescriptors) {
        String fullName = deviceDescriptor.getName();
        Matcher matcher = PATTERN.matcher(fullName);
        if (matcher.matches()) {
          String type = matcher.group(1);
          int card = Integer.parseInt(matcher.group(2));
          int unit = Integer.parseInt(matcher.group(3));
          int port = Integer.parseInt(matcher.group(4));
          String name = type + "-" + card + "-" + unit;
          MidiDeviceBundle deviceBundle = devices.get(name);
          if (deviceBundle == null) {
            deviceBundle = new MidiDeviceBundle(type, card, unit);
            devices.put(name, deviceBundle);
          }
          MidiDevice midiDevice = MidiSystem.getMidiDevice(deviceDescriptor);
          if (midiDevice.getMaxReceivers() != 0) {
            deviceBundle.addOutput(midiDevice, port);
          }
          if (midiDevice.getMaxTransmitters() != 0) {
            deviceBundle.addInput(midiDevice, port);
          }
        }
      }
      return devices;
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  private MidiConfiguration readConfiguration(MidiDeviceBundle device) {
    String home = System.getProperty("user.home");
    String fileName = device.getType() + ".configuration";
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
    System.out.println("Cannot find configuration for " + device.getType() + ", using default");
    return new MidiConfiguration();
  }

}