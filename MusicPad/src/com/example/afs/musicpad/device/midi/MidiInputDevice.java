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

public class MidiInputDevice {

  private MidiDevice midiDevice;
  private int subdevice;

  public MidiInputDevice(MidiDevice midiDevice, int subdevice) {
    this.midiDevice = midiDevice;
    this.subdevice = subdevice;
  }

  public MidiDevice getMidiDevice() {
    return midiDevice;
  }

  public int getSubdevice() {
    return subdevice;
  }

  @Override
  public String toString() {
    return "MidiInputDevice [midiDevice=" + midiDevice + ", subdevice=" + subdevice + "]";
  }

}
