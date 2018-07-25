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

public class OnTransportNoteOn implements Message {

  private int channel;
  private int midiNote;
  private int velocity;

  public OnTransportNoteOn(int channel, int midiNote, int velocity) {
    this.channel = channel;
    this.midiNote = midiNote;
    this.velocity = velocity;
  }

  public int getChannel() {
    return channel;
  }

  public int getMidiNote() {
    return midiNote;
  }

  public int getVelocity() {
    return velocity;
  }

  @Override
  public String toString() {
    return "OnTransportNoteOn [channel=" + channel + ", midiNote=" + midiNote + ", velocity=" + velocity + "]";
  }

}