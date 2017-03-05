// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import com.example.afs.musicpad.song.Note;

public class NoteEvent implements Comparable<NoteEvent> {

  public enum Type {
    NOTE_OFF, NOTE_ON, // NB: order is used by compareTo
  }

  private NoteEvent.Type type;
  private long tick;
  private Note note;

  public NoteEvent(NoteEvent.Type type, long tick, Note note) {
    this.type = type;
    this.tick = tick;
    this.note = note;
  }

  @Override
  public int compareTo(NoteEvent that) {
    long deltaTick = this.tick - that.tick;
    if (deltaTick != 0) {
      return deltaTick < 0 ? -1 : +1;
    }
    int deltaType = this.type.ordinal() - that.type.ordinal();
    if (deltaType != 0) {
      return deltaType;
    }
    if (this.note != null && that.note != null) {
      int deltaNote = this.note.compareTo(that.note);
      if (deltaNote != 0) {
        return deltaNote;
      }
    }
    return 0;
  }

  public Note getNote() {
    return note;
  }

  public long getTick() {
    return tick;
  }

  public NoteEvent.Type getType() {
    return type;
  }

  @Override
  public String toString() {
    return "NoteEvent [note=" + note + ", tick=" + tick + "]";
  }
}