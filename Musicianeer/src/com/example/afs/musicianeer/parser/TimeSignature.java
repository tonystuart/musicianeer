// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.parser;

public class TimeSignature {
  private long tick;
  private int measure;
  private int beatsPerMeasure;
  private int beatUnit;

  public TimeSignature(long tick, int measure, int beatsPerMeasure, int beatUnit) {
    this.tick = tick;
    this.measure = measure;
    this.beatsPerMeasure = beatsPerMeasure;
    this.beatUnit = beatUnit;
  }

  public int getBeatsPerMeasure() {
    return beatsPerMeasure;
  }

  public int getBeatUnit() {
    return beatUnit;
  }

  public int getMeasure() {
    return measure;
  }

  public long getTick() {
    return tick;
  }

  @Override
  public String toString() {
    return "TimeSignature [tick=" + tick + ", measure=" + measure + ", beatsPerMeasure=" + beatsPerMeasure + ", beatUnit=" + beatUnit + "]";
  }
}