// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.task.Message;

public class OnMidiLibraryRefresh implements Message {

  private MidiLibrary midiLibrary;

  public OnMidiLibraryRefresh(MidiLibrary midiLibrary) {
    this.midiLibrary = midiLibrary;
  }

  public MidiLibrary getMidiLibrary() {
    return midiLibrary;
  }

  @Override
  public String toString() {
    return "OnMidiLibrary [midiLibrary=" + midiLibrary + "]";
  }

}
