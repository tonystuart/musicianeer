// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import javax.sound.midi.MidiDevice;

import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class MidiDeviceBundle {

  private String type;
  private int card;
  private int device;

  private RandomAccessList<MidiInputDevice> inputDevices = new DirectList<>();
  private RandomAccessList<MidiOutputDevice> outputDevices = new DirectList<>();

  public MidiDeviceBundle(String type, int card, int device) {
    this.type = type;
    this.card = card;
    this.device = device;
  }

  public void addInput(MidiDevice midiDevice, int subdevice) {
    inputDevices.add(new MidiInputDevice(midiDevice, subdevice));
  }

  public void addOutput(MidiDevice midiDevice, int subdevice) {
    outputDevices.add(new MidiOutputDevice(midiDevice, subdevice));
  }

  public int getCard() {
    return card;
  }

  public int getDevice() {
    return device;
  }

  public RandomAccessList<MidiInputDevice> getInputDevices() {
    return inputDevices;
  }

  public RandomAccessList<MidiOutputDevice> getOutputDevices() {
    return outputDevices;
  }

  public String getType() {
    return type;
  }

  @Override
  public String toString() {
    return "MidiDeviceBundle [type=" + type + ", card=" + card + ", device=" + device + ", inputDevices=" + inputDevices + ", outputDevices=" + outputDevices + "]";
  }

}