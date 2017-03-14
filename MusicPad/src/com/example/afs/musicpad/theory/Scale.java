// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.theory;

import java.util.Arrays;

public class Scale {

  private String name;
  private int[] intervals;

  public Scale(String name, int... intervals) {
    this.name = name;
    this.intervals = intervals;
  }

  public int getInterval(int degree) {
    return intervals[degree % intervals.length];
  }

  public int[] getIntervals() {
    return intervals;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Scale [name=" + name + ", intervals=" + Arrays.toString(intervals) + "]";
  }

}
