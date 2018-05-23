// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.task.Message;

public class OnNoteEvent implements Message, Comparable<OnNoteEvent> {

  public enum Type { // Ordinal is used by compareTo
    TICK, //
    CUE_NOTE_ON, //
    NOTE_OFF, //
    NOTE_ON, // 
  }

  private Type type;
  private long tick;
  private int beatsPerMinute;
  private Note note;

  public OnNoteEvent(Type type, long tick, int beatsPerMinute) {
    this.type = type;
    this.tick = tick;
    this.beatsPerMinute = beatsPerMinute;
  }

  public OnNoteEvent(Type type, long tick, Note note) {
    this.type = type;
    this.tick = tick;
    this.note = note;
    this.beatsPerMinute = note.getBeatsPerMinute();
  }

  @Override
  public int compareTo(OnNoteEvent that) {
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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    OnNoteEvent other = (OnNoteEvent) obj;
    if (note == null) {
      if (other.note != null) {
        return false;
      }
    } else if (!note.equals(other.note)) {
      return false;
    }
    if (tick != other.tick) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    return true;
  }

  public int getBeatsPerMinute() {
    return beatsPerMinute;
  }

  public Note getNote() {
    return note;
  }

  public long getTick() {
    return tick;
  }

  public Type getType() {
    return type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((note == null) ? 0 : note.hashCode());
    result = prime * result + (int) (tick ^ (tick >>> 32));
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "NoteEvent [note=" + note + ", tick=" + tick + "]";
  }
}