// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import com.example.afs.musicpad.util.Tick;

public class TickScheduler {

  public static final long INITIALIZE_ON_NEXT_EVENT = -1;

  protected long baseTick;
  protected long baseTimeMillis;
  protected int percentTempo = 100;

  public TickScheduler() {
    this(0);
  }

  public TickScheduler(long baseTick) {
    this(baseTick, INITIALIZE_ON_NEXT_EVENT);
  }

  public TickScheduler(long baseTick, long baseTimeMillis) {
    this.baseTick = baseTick;
    this.baseTimeMillis = baseTimeMillis;
  }

  public long getEventTimeMillis(long noteTick, int beatsPerMinute) {
    if (baseTimeMillis == INITIALIZE_ON_NEXT_EVENT) {
      baseTimeMillis = System.currentTimeMillis();
    }
    long deltaTick = noteTick - baseTick;
    deltaTick = (deltaTick * 100) / percentTempo;
    long deltaMillis = Tick.convertTickToMillis(beatsPerMinute, deltaTick);
    long eventTimeMillis = baseTimeMillis + deltaMillis;
    // Update base values to handle changes in beats per minute
    baseTick = noteTick;
    baseTimeMillis = eventTimeMillis;
    return eventTimeMillis;
  }

  public int getPercentTempo() {
    return percentTempo;
  }

  public void resetAll() {
    this.baseTick = 0;
    this.baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
  }

  public void resetBaseTime() {
    this.baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
  }

  public void setBaseTimeMillis(long baseTimeMillis) {
    this.baseTimeMillis = baseTimeMillis;
  }

  public void setPercentTempo(int percentTempo) {
    this.percentTempo = percentTempo;
  }

}
