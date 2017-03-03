// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

public class Contour extends Item<Contour> {
  private int midiNote;
  private long duration;

  public Contour(long tick) {
    super(tick);
  }

  public Contour(long tick, int midiNote, long duration) {
    super(tick);
    this.midiNote = midiNote;
    this.duration = duration;
  }

  public long getDuration() {
    return duration;
  }

  public int getMidiNote() {
    return midiNote;
  }

  @Override
  public long getTick() {
    return tick;
  }

  @Override
  public String toString() {
    return "Contour [tick=" + tick + ", midiNote=" + midiNote + ", duration=" + duration + "]";
  }

}