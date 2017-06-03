// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.device.common.DeviceBundle;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.device.common.WatcherBehavior;

public class MidiWatcherBehavior implements WatcherBehavior {

  private static final Pattern PATTERN = Pattern.compile("^(.*) \\[hw\\:([0-9]+),([0-9]+),([0-9]+)\\]$");

  @Override
  public Controller attachDevice(DeviceHandler deviceHandler, DeviceBundle deviceBundle) {
    System.out.println("Attaching MIDI device " + deviceHandler.getDeviceName());
    Controller controller = new MidiController(deviceHandler, deviceBundle);
    return controller;
  }

  @Override
  public void detachDevice(String name, Controller controller) {
    System.out.println("Detaching MIDI device " + name);
  }

  @Override
  public Map<String, DeviceBundle> getDevices() {
    try {
      Map<String, DeviceBundle> devices = new HashMap<>();
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
          MidiDeviceBundle deviceBundle = (MidiDeviceBundle) devices.get(name);
          if (deviceBundle == null) {
            deviceBundle = new MidiDeviceBundle(type, card, unit);
            DeviceBundle x = deviceBundle;
            devices.put(name, x);
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

}