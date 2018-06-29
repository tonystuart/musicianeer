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

public class MidiInputDevice {

  private MidiDevice midiDevice;
  private int port;

  public MidiInputDevice(MidiDevice midiDevice, int port) {
    this.midiDevice = midiDevice;
    this.port = port;
  }

  public MidiDevice getMidiDevice() {
    return midiDevice;
  }

  public int getPort() {
    return port;
  }

  @Override
  public String toString() {
    return "MidiInputDevice [midiDevice=" + midiDevice + ", port=" + port + "]";
  }

}
