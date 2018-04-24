// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import com.example.afs.musicpad.device.midi.MidiDeviceBundle;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.MessageTask;

public class MidiWatcher extends MessageTask {

  private static final Pattern PATTERN = Pattern.compile("^(.*) \\[hw\\:([0-9]+),([0-9]+),([0-9]+)\\]$");

  private Map<String, MidiController> oldDevices = new HashMap<>();

  public MidiWatcher(MessageBroker broker) {
    super(broker, 1000);
  }

  public Set<String> getDeviceNames() {
    Set<String> devices = new HashSet<>();
    Info[] deviceDescriptors = MidiSystem.getMidiDeviceInfo();
    for (Info deviceDescriptor : deviceDescriptors) {
      String fullName = deviceDescriptor.getName();
      Matcher matcher = PATTERN.matcher(fullName);
      if (matcher.matches()) {
        String type = matcher.group(1);
        int card = Integer.parseInt(matcher.group(2));
        int unit = Integer.parseInt(matcher.group(3));
        String name = type + "-" + card + "-" + unit;
        devices.add(name);
      }
    }
    return devices;
  }

  @Override
  public void onTimeout() throws InterruptedException {
    Set<String> newDeviceNames = getDeviceNames();
    Iterator<Entry<String, MidiController>> oldIterator = oldDevices.entrySet().iterator();
    while (oldIterator.hasNext()) {
      Entry<String, MidiController> next = oldIterator.next();
      String oldDeviceName = next.getKey();
      if (!newDeviceNames.contains(oldDeviceName)) {
        next.getValue().detach();
        oldIterator.remove();
      }
    }
    for (String newDeviceName : newDeviceNames) {
      if (!oldDevices.containsKey(newDeviceName)) {
        MidiDeviceBundle midiDeviceBundle = getMidiDeviceBundle(newDeviceName);
        MidiController midiController = new MidiController(tsGetBroker(), newDeviceName, midiDeviceBundle);
        midiController.tsStart();
        oldDevices.put(newDeviceName, midiController);
      }
    }
  }

  private MidiDeviceBundle getMidiDeviceBundle(String deviceName) {
    try {
      MidiDeviceBundle midiDeviceBundle = null;
      Info[] deviceDescriptors = MidiSystem.getMidiDeviceInfo();
      for (Info deviceDescriptor : deviceDescriptors) {
        String fullName = deviceDescriptor.getName();
        Matcher matcher = PATTERN.matcher(fullName);
        if (matcher.matches()) {
          String type = matcher.group(1);
          int card = Integer.parseInt(matcher.group(2));
          int unit = Integer.parseInt(matcher.group(3));
          String name = type + "-" + card + "-" + unit;
          if (deviceName.equals(name)) {
            if (midiDeviceBundle == null) {
              midiDeviceBundle = new MidiDeviceBundle(type, card, unit);
            }
            int port = Integer.parseInt(matcher.group(4));
            MidiDevice midiDevice = MidiSystem.getMidiDevice(deviceDescriptor);
            if (midiDevice.getMaxReceivers() != 0) {
              midiDeviceBundle.addOutput(midiDevice, port);
            }
            if (midiDevice.getMaxTransmitters() != 0) {
              midiDeviceBundle.addInput(midiDevice, port);
            }
          }
        }
      }
      return midiDeviceBundle;
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

}