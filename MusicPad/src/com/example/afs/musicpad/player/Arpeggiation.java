// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.Collection;

import com.example.afs.musicpad.song.Note;

public class Arpeggiation {

  public static class TickInterval {

    private long start;
    private long duration;
    private int beatsPerMinute;

    public TickInterval(long start, long duration, int beatsPerMinute) {
      this.start = start;
      this.duration = duration;
      this.beatsPerMinute = beatsPerMinute;
    }

    public int getBeatsPerMinute() {
      return beatsPerMinute;
    }

    public long getDuration() {
      return duration;
    }

    public long getStart() {
      return start;
    }

    @Override
    public String toString() {
      return "TickInterval [start=" + start + ", duration=" + duration + ", beatsPerMinute=" + beatsPerMinute + "]";
    }

  }

  private TickInterval[] tickIntervals;

  public Arpeggiation(Collection<Note> notes) {
    int index = 0;
    long baseTick = -1;
    tickIntervals = new TickInterval[notes.size()];
    for (Note note : notes) {
      long tick = note.getTick();
      if (baseTick == -1) {
        baseTick = tick;
      }
      long duration = note.getDuration();
      int beatsPerMinute = note.getBeatsPerMinute();
      tickIntervals[index++] = new TickInterval(tick - baseTick, duration, beatsPerMinute);
    }
  }

  public TickInterval[] getTickIntervals() {
    return tickIntervals;
  }

}
