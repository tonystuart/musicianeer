// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.theory;

import java.util.Arrays;

import com.example.afs.musicianeer.analyzer.Names;

public class SoundType implements Comparable<SoundType> {

  private String name;
  private int[] chromaticNotes;

  public SoundType(int root, Intervals intervals) {
    name = Names.getNoteName(root) + intervals.getName();
    int[] intervalArray = intervals.getIntervals();
    chromaticNotes = new int[intervalArray.length];
    for (int i = 0; i < chromaticNotes.length; i++) {
      int chromaticNote = root + intervalArray[i];
      chromaticNotes[i] = chromaticNote;
    }
  }

  public SoundType(String name, int... chromaticNotes) {
    this.name = name;
    this.chromaticNotes = chromaticNotes;
  }

  @Override
  public int compareTo(SoundType that) {
    int limit = Math.min(this.chromaticNotes.length, that.chromaticNotes.length);
    for (int i = 0; i < limit; i++) {
      int deltaSemitone = this.chromaticNotes[i] - that.chromaticNotes[i];
      if (deltaSemitone != 0) {
        return deltaSemitone;
      }
    }
    return this.chromaticNotes.length - that.chromaticNotes.length;
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
    SoundType other = (SoundType) obj;
    if (!Arrays.equals(chromaticNotes, other.chromaticNotes)) {
      return false;
    }
    return true;
  }

  public int[] getChromaticNotes() {
    return chromaticNotes;
  }

  public int getLength() {
    return chromaticNotes.length;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(chromaticNotes);
    return result;
  }

  @Override
  public String toString() {
    return "SoundType [name=" + name + ", chromaticNotes=" + Arrays.toString(chromaticNotes) + "]";
  }

}