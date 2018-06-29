// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.util;

import com.example.afs.musicianeer.song.Default;

public class Tick {
  public static final long convertMillisToTicks(int bpm, long millis) {
    double ticksPerMilli = (bpm * Default.TICKS_PER_BEAT) / 60000d;
    long ticks = (long) (ticksPerMilli * millis);
    return ticks;
  }

  public static final long convertTickToMillis(int bpm, long tick) {
    double millisPerTick = 60000d / (bpm * Default.TICKS_PER_BEAT);
    long millis = (long) (millisPerTick * tick);
    return millis;
  }

}
