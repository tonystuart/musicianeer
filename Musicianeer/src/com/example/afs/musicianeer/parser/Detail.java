// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.parser;

import java.util.HashMap;
import java.util.Map;

import com.example.afs.musicianeer.song.Default;

public class Detail {

  private static final int CONTOUR_MINIMUM = Default.TICKS_PER_BEAT / 8;
  private static final int START_INDEX_RESOLUTION = Default.TICKS_PER_BEAT / 8;

  private long gapTicks;
  private long previousTick;
  private long previousStartTick;
  private long concurrentTicks;
  private long contourTick;
  private int program;
  private int startIndex;

  private ActiveNote contourNote;
  private Map<Integer, ActiveNote> activeNotes = new HashMap<>();
  private int endIndex;

  public void add(long tick, int midiNote, int velocity) {
    if (tick > (previousStartTick + START_INDEX_RESOLUTION)) {
      startIndex++;
    }
    int activeNoteCount = activeNotes.size();
    long deltaTick = tick - previousTick;
    if (activeNoteCount == 0) {
      gapTicks = gapTicks + deltaTick;
    } else {
      concurrentTicks = concurrentTicks + activeNoteCount * deltaTick;
    }
    activeNotes.put(midiNote, new ActiveNote(tick, midiNote, program, velocity, startIndex));
    updateContour(tick);
    previousTick = tick;
    previousStartTick = tick;
  }

  public boolean allNotesAreOff() {
    return activeNotes.size() == 0;
  }

  public ActiveNote getActiveNote(int midiNote) {
    return activeNotes.get(midiNote);
  }

  public int getConcurrency() {
    int concurrency = 0;
    long totalTicks = previousTick;
    long occupancyTicks = totalTicks - gapTicks;
    if (occupancyTicks != 0) {
      concurrency = (int) ((concurrentTicks * 100) / occupancyTicks);
    }
    return concurrency;
  }

  public int getEndIndex() {
    return endIndex;
  }

  public int getOccupancy() {
    int occupancy = 0;
    long totalTicks = previousTick;
    if (totalTicks != 0) {
      long occupancyTicks = totalTicks - gapTicks;
      occupancy = (int) ((occupancyTicks * 100) / totalTicks);
    }
    return occupancy;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public void remove(long tick, int midiNote) {
    long deltaTick = tick - previousTick;
    int activeNoteCount = activeNotes.size();
    concurrentTicks = concurrentTicks + activeNoteCount * deltaTick;
    activeNotes.remove(midiNote);
    updateContour(tick);
    previousTick = tick;
    if (allNotesAreOff()) {
      endIndex++;
    }
  }

  public void setProgram(int program) {
    this.program = program;
  }

  private ActiveNote findHighestNote(Map<Integer, ActiveNote> activeNotes) {
    ActiveNote highestNote = null;
    for (ActiveNote tickEvent : activeNotes.values()) {
      if (highestNote == null || tickEvent.getMidiNote() > highestNote.getMidiNote()) {
        highestNote = tickEvent;
      }
    }
    return highestNote;
  }

  private void updateContour(long tick) {
    ActiveNote highestNote = findHighestNote(activeNotes);
    if (highestNote != contourNote) {
      if (contourNote != null) {
        long xduration = tick - contourTick;
        if (xduration > CONTOUR_MINIMUM) {
          // contour.add(new Contour(xtick, contourNote.getMidiNote(), xduration));
        }
      }
      contourTick = tick;
      contourNote = highestNote;
    }
  }
}
