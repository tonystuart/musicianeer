// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class MidiDeviceBundle {

  private String name;
  private RandomAccessList<MidiInputDevice> inputDevices = new DirectList<>();
  private RandomAccessList<MidiOutputDevice> outputDevices = new DirectList<>();

  public MidiDeviceBundle(String name) {
    this.name = name;
  }

  public void addInput(MidiInputDevice midiDevice) {
    inputDevices.add(midiDevice);
  }

  public void addOutput(MidiOutputDevice outputDevice) {
    outputDevices.add(outputDevice);
  }

  public RandomAccessList<MidiInputDevice> getInputDevices() {
    return inputDevices;
  }

  public String getName() {
    return name;
  }

  public RandomAccessList<MidiOutputDevice> getOutputDevices() {
    return outputDevices;
  }

  @Override
  public String toString() {
    return "Device [name=" + name + ", inputDevices=" + inputDevices + ", outputDevices=" + outputDevices + "]";
  }

}