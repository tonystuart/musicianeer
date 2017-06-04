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

import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.theory.IntervalSet;
import com.example.afs.musicpad.theory.SoundType;

public class Sound implements Comparable<Sound> {

  private int[] midiNotes;

  private SoundType soundType;
  private Arpeggiation arpeggiation;

  public Sound(Collection<Note> notes) {
    int noteIndex = 0;
    this.midiNotes = new int[notes.size()];
    if (requiresArpeggiation(notes)) {
      this.arpeggiation = new Arpeggiation(notes);
    }
    for (Note note : notes) {
      this.midiNotes[noteIndex++] = note.getMidiNote();
    }
  }

  public Sound(int... notes) {
    this.midiNotes = notes;
  }

  @Override
  public int compareTo(Sound that) {
    int controllingLength = Math.min(this.midiNotes.length, that.midiNotes.length);
    for (int i = 0; i < controllingLength; i++) {
      int delta = this.midiNotes[i] - that.midiNotes[i];
      if (delta != 0) {
        return delta;
      }
    }
    return this.midiNotes.length - that.midiNotes.length;
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
    if (!Arrays.equals(midiNotes, other.midiNotes)) {
      return false;
    }
    return true;
  }

  public Arpeggiation getArpeggiation() {
    return arpeggiation;
  }

  public int[] getMidiNotes() {
    return midiNotes;
  }

  public SoundType getSoundType() {
    if (soundType == null) {
      IntervalSet intervalSet = new IntervalSet();
      for (int i = 0; i < midiNotes.length; i++) {
        intervalSet.add(midiNotes[i]);
      }
      soundType = intervalSet.getSoundType();
    }
    return soundType;
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
    return "Sound [soundType=" + getSoundType() + ", midiNotes=" + Arrays.toString(midiNotes) + "]";
  }

  private boolean requiresArpeggiation(Collection<Note> notes) {
    long firstTick = Long.MAX_VALUE;
    long lastTick = Long.MIN_VALUE;
    for (Note note : notes) {
      long tick = note.getTick();
      if (tick < firstTick) {
        firstTick = tick;
      }
      if (tick > lastTick) {
        lastTick = tick;
      }
    }
    long spread = lastTick - firstTick;
    boolean requiresArpeggiation = spread > (Default.TICKS_PER_BEAT / 4);
    return requiresArpeggiation;
  }

}