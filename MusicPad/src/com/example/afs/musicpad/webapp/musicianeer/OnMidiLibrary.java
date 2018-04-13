// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.message.TypedMessage;
import com.example.afs.musicpad.midi.MidiLibrary;

public class OnMidiLibrary extends TypedMessage {

  private MidiLibrary midiLibrary;

  public OnMidiLibrary(MidiLibrary midiLibrary) {
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
