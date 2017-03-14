// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

import com.example.afs.musicpad.theory.ChordType;

public class Chord extends Item<Chord> {
  private ChordType chordType;
  private long duration;

  public Chord(ChordType chordType, long tick, long duration) {
    super(tick);
    this.chordType = chordType;
    this.duration = duration;
  }

  public Chord(long tick) {
    super(tick);
  }

  public ChordType getChordType() {
    return chordType;
  }

  public long getDuration() {
    return duration;
  }

  @Override
  public long getTick() {
    return tick;
  }

  @Override
  public String toString() {
    return "ChordInstance [chordType=" + chordType + ", tick=" + tick + ", duration=" + duration + "]";
  }
}