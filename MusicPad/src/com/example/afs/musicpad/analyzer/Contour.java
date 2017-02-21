// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import com.example.afs.musicpad.song.Note;

public class Contour {
  private long tick;
  private Note note;
  private long duration;

  public Contour(long tick, Note note, long duration) {
    this.tick = tick;
    this.note = note;
    this.duration = duration;
  }

  public long getDuration() {
    return duration;
  }

  public Note getNote() {
    return note;
  }

  public long getTick() {
    return tick;
  }

  @Override
  public String toString() {
    return "Contour [tick=" + tick + ", note=" + note + ", duration=" + duration + "]";
  }

}