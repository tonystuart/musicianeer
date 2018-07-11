// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.message;

import com.example.afs.musicianeer.main.MusicianeerView.LedState;
import com.example.afs.musicianeer.task.Message;

public class OnSetMidiNoteLed implements Message {

  private int channel;
  private int midiNote;
  private LedState state;

  public OnSetMidiNoteLed(int channel, int midiNote, LedState state) {
    this.channel = channel;
    this.midiNote = midiNote;
    this.state = state;
  }

  public int getChannel() {
    return channel;
  }

  public int getMidiNote() {
    return midiNote;
  }

  public LedState getState() {
    return state;
  }

  @Override
  public String toString() {
    return "OnSetMidiNoteLed [channel=" + channel + ", midiNote=" + midiNote + ", state=" + state + "]";
  }

}
