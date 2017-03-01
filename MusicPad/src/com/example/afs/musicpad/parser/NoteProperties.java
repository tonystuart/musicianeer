// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.parser;

public class NoteProperties {
  private long tick;
  private int midiNote;
  private int instrument;
  private int velocity;

  public NoteProperties(long tick, int midiNote, int instrument, int velocity) {
    this.tick = tick;
    this.midiNote = midiNote;
    this.instrument = instrument;
    this.velocity = velocity;
  }

  public int getInstrument() {
    return instrument;
  }

  public int getMidiNote() {
    return midiNote;
  }

  public long getTick() {
    return tick;
  }

  public int getVelocity() {
    return velocity;
  }

  @Override
  public String toString() {
    return "NoteProperties [tick=" + tick + ", midiNote=" + midiNote + ", instrument=" + instrument + ", velocity=" + velocity + "]";
  }
}