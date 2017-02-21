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

public class TickEvent implements Comparable<TickEvent> {
  private static int next = 1;
  private int counter;
  private Note note;
  private long tick;
  private TickEvent noteOnEvent;

  public TickEvent(Note note) {
    this.note = note;
    this.tick = note.getTick();
    this.counter = next++;
  }

  public TickEvent(Note note, TickEvent noteOnEvent) {
    this.note = note;
    this.tick = note.getTick() + note.getDuration();
    this.noteOnEvent = noteOnEvent;
    this.counter = next++;
  }

  @Override
  public int compareTo(TickEvent that) {
    long deltaTick = this.tick - that.tick;
    if (deltaTick != 0) {
      return (int) deltaTick;
    }
    int deltaType = (this.note == null ? 1 : 2) - (that.note == null ? 1 : 2); // off before on
    if (deltaType != 0) {
      return deltaType;
    }
    int deltaCounter = this.counter - that.counter;
    if (deltaCounter != 0) {
      return deltaCounter;
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
    TickEvent other = (TickEvent) obj;
    if (note == null) {
      if (other.note != null) {
        return false;
      }
    } else if (!note.equals(other.note)) {
      return false;
    }
    if (counter != other.counter) {
      return false;
    }
    if (tick != other.tick) {
      return false;
    }
    return true;
  }

  public long getEndTick() {
    return tick + note.getDuration();
  }

  public Note getNote() {
    return note;
  }

  public TickEvent getNoteOnEvent() {
    return noteOnEvent;
  }

  public long getTick() {
    return tick;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((note == null) ? 0 : note.hashCode());
    result = prime * result + counter;
    result = (int) (prime * result + tick);
    return result;
  }

  public boolean isNoteOn() {
    return noteOnEvent == null;
  }

  public void setTick(int tick) {
    this.tick = tick;
  }

  @Override
  public String toString() {
    return "TickEvent [tick=" + tick + ", counter=" + counter + ", note=" + note + ", noteOnEvent=" + noteOnEvent + "]";
  }

}