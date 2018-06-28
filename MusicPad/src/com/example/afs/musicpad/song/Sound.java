// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

import java.util.Iterator;

import com.example.afs.musicianeer.analyzer.Names;
import com.example.afs.musicpad.theory.IntervalSet;
import com.example.afs.musicpad.theory.SoundType;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

// NB: equals, hashCode and compareTo use only the midiNote values in notes.

public class Sound implements Comparable<Sound>, Iterable<Note> {

  private long beginTick = Long.MAX_VALUE;
  private long endTick = Long.MIN_VALUE;

  private String noteNames;
  private SoundType soundType;
  private RandomAccessList<Note> notes = new DirectList<>();

  public Sound() {
  }

  public Sound(Note... notes) {
    for (Note note : notes) {
      add(note);
    }
  }

  public void add(Note note) {
    notes.add(note);
    soundType = null;
    long tick = note.getTick();
    beginTick = Math.min(beginTick, tick);
    endTick = Math.max(endTick, tick + note.getDuration());
    noteNames = null;
  }

  @Override
  public int compareTo(Sound that) {
    int thisNoteCount = this.notes.size();
    int thatNoteCount = that.notes.size();
    int controllingLength = Math.min(thisNoteCount, thatNoteCount);
    for (int i = 0; i < controllingLength; i++) {
      int delta = this.notes.get(i).getMidiNote() - that.notes.get(i).getMidiNote();
      if (delta != 0) {
        return delta;
      }
    }
    return thisNoteCount - thatNoteCount;
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
    Sound other = (Sound) obj;
    if (compareTo(other) != 0) {
      return false;
    }
    return true;
  }

  public long getBeginTick() {
    return beginTick;
  }

  public long getEndTick() {
    return endTick;
  }

  public String getName() {
    return getSoundType().getName();
  }

  public String getNoteNames() {
    if (noteNames == null) {
      StringBuilder s = new StringBuilder();
      for (Note note : notes) {
        if (s.length() > 0) {
          s.append("/");
        }
        s.append(Names.formatNoteName(note.getMidiNote()));
      }
      noteNames = s.toString();
    }
    return noteNames;
  }

  public RandomAccessList<Note> getNotes() {
    return notes;
  }

  public SoundType getSoundType() {
    if (soundType == null) {
      int noteCount = notes.size();
      IntervalSet intervalSet = new IntervalSet();
      for (int i = 0; i < noteCount; i++) {
        Note note = notes.get(i);
        intervalSet.add(note.getMidiNote());
      }
      soundType = intervalSet.getSoundType();
    }
    return soundType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    int noteCount = notes.size();
    for (int i = 0; i < noteCount; i++) {
      Note note = notes.get(i);
      result = prime * result + note.getMidiNote();
    }
    return result;
  }

  @Override
  public Iterator<Note> iterator() {
    return notes.iterator();
  }

  @Override
  public String toString() {
    return "Sound [soundType=" + getSoundType() + ", notes=" + notes + "]";
  }

}