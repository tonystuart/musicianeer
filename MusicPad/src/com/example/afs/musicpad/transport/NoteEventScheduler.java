// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import com.example.afs.musicpad.util.Scheduler;
import com.example.afs.musicpad.util.Tick;

public class NoteEventScheduler implements Scheduler<NoteEvent> {

  public static final long INITIALIZE_ON_NEXT_EVENT = -1;

  private long baseTick;
  private long baseTimeMillis;
  private int percentTempo = 100;

  public NoteEventScheduler() {
    this(0);
  }

  public NoteEventScheduler(long baseTick) {
    this(baseTick, INITIALIZE_ON_NEXT_EVENT);
  }

  public NoteEventScheduler(long baseTick, long baseTimeMillis) {
    this.baseTick = baseTick;
    this.baseTimeMillis = baseTimeMillis;
  }

  @Override
  public long getEventTimeMillis(NoteEvent noteEvent) {
    if (baseTimeMillis == INITIALIZE_ON_NEXT_EVENT) {
      baseTimeMillis = System.currentTimeMillis();
    }
    long noteTick = noteEvent.getTick();
    long deltaTick = noteTick - baseTick;
    deltaTick = (deltaTick * 100) / percentTempo;
    long deltaMillis = Tick.convertTickToMillis(noteEvent.getNote().getBeatsPerMinute(), deltaTick);
    long eventTimeMillis = baseTimeMillis + deltaMillis;
    // Update base values to handle changes in beats per minute
    baseTick = noteTick;
    baseTimeMillis = eventTimeMillis;
    return eventTimeMillis;
  }

  public int getPercentTempo() {
    return percentTempo;
  }

  public void reset() {
    this.baseTick = 0;
    this.baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
  }

  public void setBaseTimeMillis(long baseTimeMillis) {
    this.baseTimeMillis = baseTimeMillis;
  }

  public void setPercentTempo(int percentTempo) {
    this.percentTempo = percentTempo;
  }
}