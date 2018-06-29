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
  private int beatsPerMeasure;
  private int beatUnit;

  public TimeSignature(int beatsPerMeasure, int beatUnit) {
    this.beatsPerMeasure = beatsPerMeasure;
    this.beatUnit = beatUnit;
  }

  public int getBeatsPerMeasure() {
    return beatsPerMeasure;
  }

  public int getBeatUnit() {
    return beatUnit;
  }

  @Override
  public String toString() {
    return "TimeSignature [beatsPerMeasure=" + beatsPerMeasure + ", beatUnit=" + beatUnit + "]";
  }
}