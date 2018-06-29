// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.parser;

public class ActiveNote {
  private long tick;
  private int midiNote;
  private int program;
  private int velocity;
  private int startIndex;

  public ActiveNote(long tick, int midiNote, int program, int velocity, int startIndex) {
    this.tick = tick;
    this.midiNote = midiNote;
    this.program = program;
    this.velocity = velocity;
    this.startIndex = startIndex;
  }

  public int getMidiNote() {
    return midiNote;
  }

  public int getProgram() {
    return program;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public long getTick() {
    return tick;
  }

  public int getVelocity() {
    return velocity;
  }

  @Override
  public String toString() {
    return "NoteProperties [tick=" + tick + ", midiNote=" + midiNote + ", program=" + program + ", velocity=" + velocity + "]";
  }
}