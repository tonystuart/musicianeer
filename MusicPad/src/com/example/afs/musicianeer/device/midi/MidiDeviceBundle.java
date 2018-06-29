// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.device.midi;

import javax.sound.midi.MidiDevice;

import com.example.afs.musicianeer.util.DirectList;
import com.example.afs.musicianeer.util.RandomAccessList;

public class MidiDeviceBundle {

  private String type;
  private int card;
  private int unit;

  private RandomAccessList<MidiInputDevice> inputDevices = new DirectList<>();
  private RandomAccessList<MidiOutputDevice> outputDevices = new DirectList<>();

  public MidiDeviceBundle(String type, int card, int unit) {
    this.type = type;
    this.card = card;
    this.unit = unit;
  }

  public void addInput(MidiDevice midiDevice, int port) {
    inputDevices.add(new MidiInputDevice(midiDevice, port));
  }

  public void addOutput(MidiDevice midiDevice, int port) {
    outputDevices.add(new MidiOutputDevice(midiDevice, port));
  }

  public int getCard() {
    return card;
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

  public int getUnit() {
    return unit;
  }

  @Override
  public String toString() {
    return "MidiDeviceBundle [type=" + type + ", card=" + card + ", unit=" + unit + ", inputDevices=" + inputDevices + ", outputDevices=" + outputDevices + "]";
  }

}