// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.Arrays;
import java.util.Collection;

import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.theory.ChordType;
import com.example.afs.musicpad.theory.IntervalSet;

public class Chord {
  private ChordType chordType;
  private int[] midiNotes;

  public Chord(Collection<Note> notes) {
    int noteIndex = 0;
    this.midiNotes = new int[notes.size()];
    for (Note note : notes) {
      this.midiNotes[noteIndex++] = note.getMidiNote();
    }
  }

  public Chord(int... notes) {
    this.midiNotes = notes;
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
    Chord other = (Chord) obj;
    if (!Arrays.equals(midiNotes, other.midiNotes)) {
      return false;
    }
    return true;
  }

  public ChordType getChordType() {
    if (chordType == null) {
      IntervalSet intervalSet = new IntervalSet();
      for (int i = 0; i < midiNotes.length; i++) {
        intervalSet.add(midiNotes[i]);
      }
      chordType = intervalSet.getChordType();
    }
    return chordType;
  }

  public int[] getMidiNotes() {
    return midiNotes;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(midiNotes);
    return result;
  }

  @Override
  public String toString() {
    return "Chord [chordType=" + getChordType() + ", midiNotes=" + Arrays.toString(midiNotes) + "]";
  }

}