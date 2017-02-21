// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import java.util.NavigableSet;
import java.util.TreeSet;

import com.example.afs.musicpad.song.Note;

public class ConcurrencyAnalyzer {

  public Concurrency getConcurrency(NavigableSet<Note> notes, int channel) {
    int occupancy = 0;
    int polyphony = 0;
    if (notes.size() > 0) {
      TreeSet<TickEvent> tickEvents = new TreeSet<TickEvent>();
      for (Note note : notes) {
        if (note.getChannel() == channel) {
          TickEvent noteOnEvent = new TickEvent(note);
          TickEvent noteOffEvent = new TickEvent(note, noteOnEvent);
          tickEvents.add(noteOnEvent);
          tickEvents.add(noteOffEvent);
        }
      }
      long gapTicks = 0;
      long polyphonyTicks = 0;
      long firstTick = notes.first().getTickOfThisMeasure(); // ignore notes playing before first measure
      long lastTick = notes.last().getTickOfNextMeasure(); // ignore notes still playing after last measure
      long previousTick = firstTick;
      int activeNoteCount = 0;
      for (TickEvent tickEvent : tickEvents) {
        long tick = tickEvent.getTick();
        if (tick > lastTick) {
          tick = lastTick; // truncate note at end of last measure
        }
        if (tickEvent.isNoteOn()) {
          if (activeNoteCount == 0) {
            gapTicks += tick - previousTick;
          }
          polyphonyTicks += activeNoteCount * (tick - previousTick);
          activeNoteCount++;
        } else {
          polyphonyTicks += activeNoteCount * (tick - previousTick);
          activeNoteCount--;
        }
        previousTick = tick;
      }
      gapTicks += lastTick - previousTick;
      long totalTicks = lastTick - firstTick;
      long occupancyTicks = totalTicks - gapTicks;
      occupancy = (int) ((occupancyTicks * 100) / totalTicks);
      polyphony = (int) ((polyphonyTicks * 100) / occupancyTicks);
    }
    Concurrency concurrency = new Concurrency(occupancy, polyphony);
    return concurrency;
  }

}
