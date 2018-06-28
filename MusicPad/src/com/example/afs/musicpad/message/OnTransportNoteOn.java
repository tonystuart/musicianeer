// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.task.Message;

public class OnTransportNoteOn implements Message {

  private int channel;
  private int midiNote;

  public OnTransportNoteOn(int channel, int midiNote) {
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
    return "OnTransportNoteOn [channel=" + channel + ", midiNote=" + midiNote + "]";
  }

}