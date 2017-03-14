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

public class Intervals {

  public static final Intervals AUGMENTED = new Intervals("aug", new int[] {
      0,
      4,
      8
  });

  public static final Intervals DIMINISHED = new Intervals("dim", new int[] {
      0,
      3,
      6
  });

  public static final Intervals MAJOR = new Intervals("Maj", new int[] {
      0,
      4,
      7
  });

  public static final Intervals MAJOR_SEVENTH = new Intervals("Maj7", new int[] {
      0,
      4,
      7,
      11
  });

  public static final Intervals MAJOR_NINTH = new Intervals("Maj9", new int[] {
      0,
      4,
      7,
      11,
      14
  });

  public static final Intervals MINOR = new Intervals("min", new int[] {
      0,
      3,
      7
  });

  public static final Intervals MINOR_SEVENTH = new Intervals("min7", new int[] {
      0,
      3,
      7,
      10
  });

  public static final Intervals MINOR_NINTH = new Intervals("min9", new int[] {
      0,
      3,
      7,
      10,
      14
  });

  public static final Intervals SEVENTH = new Intervals("7", new int[] {
      0,
      4,
      7,
      10
  });

  private String name;
  private int[] intervals;

  public Intervals(String name, int[] intervals) {
    this.name = name;
    this.intervals = intervals;
  }

  public int[] getIntervals() {
    return intervals;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Intervals [name=" + name + ", intervals=" + Arrays.toString(intervals) + "]";
  }

}