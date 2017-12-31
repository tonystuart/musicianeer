// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

public final class KeyScore implements Comparable<KeyScore> {
  private int naturals;
  private int accidentals;
  private int tonicThirds;
  private int tonicTriads;
  private int tonic;
  private int relativeTonic;
  private boolean isMajor;
  private int sharpsOrFlats;
  private int rank;

  public KeyScore(int naturals, int accidentals, int tonicThirds, int tonicTriads, int tonic, int relativeTonic, boolean isMajor, int sharpsOrFlats) {
    this.naturals = naturals;
    this.accidentals = accidentals;
    this.tonicThirds = tonicThirds;
    this.tonicTriads = tonicTriads;
    this.tonic = tonic;
    this.relativeTonic = relativeTonic;
    this.isMajor = isMajor;
    this.sharpsOrFlats = sharpsOrFlats;
  }

  @Override
  public int compareTo(KeyScore that) {
    int deltaAccidentals = this.accidentals - that.accidentals; // ascending
    if (deltaAccidentals != 0) {
      return deltaAccidentals;
    }
    int deltaTonicTriads = that.tonicTriads - this.tonicTriads; // descending
    if (deltaTonicTriads != 0) {
      return deltaTonicTriads;
    }
    int deltaTonicThirds = that.tonicThirds - this.tonicThirds; // descending
    return deltaTonicThirds;
  }

  public int getAccidentals() {
    return accidentals;
  }

  public int getConfidence() {
    int confidence = 0;
    int totalCount = naturals + accidentals;
    if (totalCount != 0) {
      confidence = (naturals * 100) / totalCount;
    }
    return confidence;
  }

  public int getFlats() {
    return sharpsOrFlats < 0 ? -sharpsOrFlats : 0;
  }

  public String getKey() {
    return Names.getKeyName(tonic, isMajor, sharpsOrFlats);
  }

  public int getRank() {
    return rank;
  }

  public String getRelativeKey() {
    return Names.getKeyName(relativeTonic, !isMajor, sharpsOrFlats);
  }

  public int getSharps() {
    return sharpsOrFlats < 0 ? 0 : sharpsOrFlats;
  }

  public String getSynopsis() {
    return Names.getSynopsis(sharpsOrFlats);
  }

  public int getThirds() {
    return tonicThirds;
  }

  public int getTriads() {
    return tonicTriads;
  }

  public boolean isTieScore(KeyScore that) {
    return this.accidentals == that.accidentals && this.tonicTriads == that.tonicTriads;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  @Override
  public String toString() {
    return "[naturals=" + naturals + ", accidentals=" + accidentals + ", tonicTriads=" + tonicTriads + ", tonic=" + tonic + ", isMajor=" + isMajor + ", sharpsOrFlats=" + sharpsOrFlats + "]";
  }

}
