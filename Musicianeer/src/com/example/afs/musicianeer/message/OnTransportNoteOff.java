// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.message;

import com.example.afs.musicianeer.task.Message;

public class OnTransportNoteOff implements Message {

  private int channel;
  private int midiNote;

  public OnTransportNoteOff(int channel, int midiNote) {
    this.channel = channel;
    this.midiNote = midiNote;
  }

  public int getChannel() {
    return channel;
  }

  public int getMidiNote() {
    return midiNote;
  }

  @Override
  public String toString() {
    return "OnTransportNoteOff [channel=" + channel + ", midiNote=" + midiNote + "]";
  }

}