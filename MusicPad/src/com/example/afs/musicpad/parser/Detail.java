package com.example.afs.musicpad.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.example.afs.musicpad.song.Contour;
import com.example.afs.musicpad.song.Default;

public class Detail {

  private long gapTicks;
  private long previousTick;
  private long concurrentTicks;
  private ActiveNote contourNote;
  private TreeSet<Contour> contour = new TreeSet<>();
  private Map<Integer, ActiveNote> activeNotes = new HashMap<>();
  private int program;
  private long xtick;

  public void add(long tick, int midiNote, int velocity) {
    int activeNoteCount = activeNotes.size();
    long deltaTick = tick - previousTick;
    if (activeNoteCount == 0) {
      gapTicks = gapTicks + deltaTick;
    } else {
      concurrentTicks = concurrentTicks + activeNoteCount * deltaTick;
    }
    activeNotes.put(midiNote, new ActiveNote(tick, midiNote, program, velocity));
    updateContour(tick);
    previousTick = tick;
  }

  public boolean allNotesAreOff() {
    return activeNotes.size() == 0;
  }

  public ActiveNote get(int midiNote) {
    return activeNotes.get(midiNote);
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

  public TreeSet<Contour> getContour() {
    return contour;
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

  public void remove(long tick, int midiNote) {
    long deltaTick = tick - previousTick;
    int activeNoteCount = activeNotes.size();
    concurrentTicks = concurrentTicks + activeNoteCount * deltaTick;
    activeNotes.remove(midiNote);
    updateContour(tick);
    previousTick = tick;
  }

  public void setProgram(int program) {
    this.program = program;
  }

  private ActiveNote findHighestNote(Map<Integer, ActiveNote> groupNotes) {
    ActiveNote highestNote = null;
    for (ActiveNote tickEvent : groupNotes.values()) {
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
        long xduration = tick - xtick;
        if (xduration > Default.TICKS_PER_BEAT / 8) {
          contour.add(new Contour(xtick, contourNote.getMidiNote(), xduration));
        }
      }
      xtick = tick;
      contourNote = highestNote;
    }
  }
}
