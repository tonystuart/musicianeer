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

import com.example.afs.musicpad.analyzer.Names;

public class ChordType implements Comparable<ChordType> {

  private String name;
  private int[] commonNotes;

  public ChordType(int root, Intervals intervals) {
    name = Names.getNoteName(root) + intervals.getName();
    int[] intervalArray = intervals.getIntervals();
    commonNotes = new int[intervalArray.length];
    for (int i = 0; i < commonNotes.length; i++) {
      int commonNote = root + intervalArray[i];
      commonNotes[i] = commonNote;
    }
  }

  public ChordType(String name, int... commonNotes) {
    this.name = name;
    this.commonNotes = commonNotes;
  }

  @Override
  public int compareTo(ChordType that) {
    int limit = Math.min(this.commonNotes.length, that.commonNotes.length);
    for (int i = 0; i < limit; i++) {
      int deltaSemitone = this.commonNotes[i] - that.commonNotes[i];
      if (deltaSemitone != 0) {
        return deltaSemitone;
      }
    }
    return this.commonNotes.length - that.commonNotes.length;
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
    ChordType other = (ChordType) obj;
    if (!Arrays.equals(commonNotes, other.commonNotes)) {
      return false;
    }
    return true;
  }

  public int[] getCommonNotes() {
    return commonNotes;
  }

  public int getLength() {
    return commonNotes.length;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(commonNotes);
    return result;
  }

  @Override
  public String toString() {
    return "ChordType [name=" + name + ", commonNotes=" + Arrays.toString(commonNotes) + "]";
  }

}