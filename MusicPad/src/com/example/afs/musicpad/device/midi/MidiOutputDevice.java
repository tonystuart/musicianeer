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

public class MidiOutputDevice {
  private String name;
  private MidiDevice midiDevice;

  public MidiOutputDevice(String name, MidiDevice midiDevice) {
    this.name = name;
    this.midiDevice = midiDevice;
  }

  public MidiDevice getMidiDevice() {
    return midiDevice;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "OutputDevice [name=" + name + "]";
  }

}