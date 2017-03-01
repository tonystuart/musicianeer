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
  private NoteProperties contourNote;
  private TreeSet<Contour> contour = new TreeSet<>();
  private Map<Integer, NoteProperties> groupNotes = new HashMap<>();

  public void add(long tick, int midiNote, int velocity, int instrument) {
    NoteProperties noteProperties = new NoteProperties(tick, midiNote, instrument, velocity);
    int activeNoteCount = groupNotes.size();
    long deltaTick = tick - previousTick;
    if (activeNoteCount == 0) {
      gapTicks = gapTicks + deltaTick;
    } else {
      concurrentTicks = concurrentTicks + activeNoteCount * deltaTick;
    }
    groupNotes.put(midiNote, noteProperties);
    NoteProperties highestNote = findHighestNote(groupNotes);
    if (highestNote != contourNote) {
      if (contourNote != null) {
        if (deltaTick > Default.TICKS_PER_BEAT / 8) {
          contour.add(new Contour(previousTick, contourNote.getMidiNote(), deltaTick));
        }
      }
      contourNote = highestNote;
    }
    previousTick = tick;
  }

  public boolean allNotesAreOff() {
    return groupNotes.size() == 0;
  }

  public NoteProperties get(int midiNote) {
    return groupNotes.get(midiNote);
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

  public NoteProperties getNoteProperties(int midiNote) {
    return groupNotes.get(midiNote);
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
    int activeNoteCount = groupNotes.size();
    concurrentTicks = concurrentTicks + activeNoteCount * deltaTick;
    groupNotes.remove(midiNote);
    NoteProperties highestNote = findHighestNote(groupNotes);
    if (highestNote != contourNote) {
      if (contourNote != null) {
        if (deltaTick > Default.TICKS_PER_BEAT / 8) {
          contour.add(new Contour(previousTick, contourNote.getMidiNote(), deltaTick));
        }
      }
      contourNote = highestNote;
    }
    previousTick = tick;
  }

  private NoteProperties findHighestNote(Map<Integer, NoteProperties> groupNotes) {
    NoteProperties highestNote = null;
    for (NoteProperties tickEvent : groupNotes.values()) {
      if (highestNote == null || tickEvent.getMidiNote() > highestNote.getMidiNote()) {
        highestNote = tickEvent;
      }
    }
    return highestNote;
  }
}
