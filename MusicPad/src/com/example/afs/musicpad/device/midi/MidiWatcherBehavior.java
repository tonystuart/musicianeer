// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.device.common.WatcherBehavior;

public class MidiWatcherBehavior implements WatcherBehavior {

  private static final Pattern PATTERN = Pattern.compile("^(.*) \\[hw\\:([0-9]+),([0-9]+),([0-9]+)\\]$");

  @Override
  public Controller attachDevice(DeviceHandler deviceHandler, String deviceName) {
    System.out.println("Attaching MIDI device " + deviceName);
    MidiDeviceBundle midiDeviceBundle = getMidiDeviceBundle(deviceName);
    Controller controller = new MidiController(deviceHandler, midiDeviceBundle);
    return controller;
  }

  @Override
  public void detachDevice(String name, Controller controller) {
    System.out.println("Detaching MIDI device " + name);
  }

  @Override
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