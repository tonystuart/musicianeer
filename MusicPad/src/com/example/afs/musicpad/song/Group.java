package com.example.afs.musicpad.song;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Group {

  public static class NoteProperties {
    private long tick;
    private int midiNote;
    private int instrument;
    private int velocity;

    public NoteProperties(long tick, int midiNote, int instrument, int velocity) {
      this.tick = tick;
      this.midiNote = midiNote;
      this.instrument = instrument;
      this.velocity = velocity;
    }

    public int getInstrument() {
      return instrument;
    }

    public int getMidiNote() {
      return midiNote;
    }

    public long getTick() {
      return tick;
    }

    public int getVelocity() {
      return velocity;
    }

    @Override
    public String toString() {
      return "NoteProperties [tick=" + tick + ", midiNote=" + midiNote + ", instrument=" + instrument + ", velocity=" + velocity + "]";
    }
  }

  private static class Details {
    private long gapTicks;
    private long previousTick;
    private long concurrentTicks;
    private TreeSet<Contour> contour = new TreeSet<>();
    private Map<Integer, NoteProperties> groupNotes = new HashMap<>();

    public long getConcurrentTicks() {
      return concurrentTicks;
    }

    public TreeSet<Contour> getContour() {
      return contour;
    }

    public long getGapTicks() {
      return gapTicks;
    }

    public long getPreviousTick() {
      return previousTick;
    }

    public void setConcurrentTicks(long concurrentTicks) {
      this.concurrentTicks = concurrentTicks;
    }

    public void setGapTicks(long gapTicks) {
      this.gapTicks = gapTicks;
    }

    public void setPreviousTick(long previousTick) {
      this.previousTick = previousTick;
    }
  }

  private Details[] channelDetails = new Details[Midi.CHANNELS];
  private NoteProperties currentHighestNote;
  private long lastTick;

  public Group() {
    for (int i = 0; i < Midi.CHANNELS; i++) {
      channelDetails[i] = new Details();
    }
  }

  public void add(long tick, int channel, int midiNote, int velocity, int instrument) {
    Details details = channelDetails[channel];
    NoteProperties noteProperties = new NoteProperties(tick, midiNote, instrument, velocity);
    updateUtilizationOnAdd(tick, details);
    details.groupNotes.put(midiNote, noteProperties);
    updateContour(tick, details);
  }

  public boolean allNotesAreOff(int channel) {
    return channelDetails[channel].groupNotes.size() == 0;
  }

  public NoteProperties get(int channel, int midiNote) {
    return channelDetails[channel].groupNotes.get(midiNote);
  }

  public int getConcurrency(int channel) {
    int concurrency = 0;
    Details details = channelDetails[channel];
    long totalTicks = details.getPreviousTick();
    long occupancyTicks = totalTicks - details.getGapTicks();
    if (occupancyTicks != 0) {
      concurrency = (int) ((details.getConcurrentTicks() * 100) / occupancyTicks);
    }
    return concurrency;
  }

  public TreeSet<Contour> getContour(int channel) {
    return channelDetails[channel].contour;
  }

  public int getOccupancy(int channel) {
    int occupancy = 0;
    Details details = channelDetails[channel];
    long totalTicks = details.getPreviousTick();
    if (totalTicks != 0) {
      long occupancyTicks = totalTicks - details.getGapTicks();
      occupancy = (int) ((occupancyTicks * 100) / totalTicks);
    }
    return occupancy;
  }

  public void remove(long tick, int channel, int midiNote) {
    Details details = channelDetails[channel];
    updateUtilizationOnRemove(tick, details);
    details.groupNotes.remove(midiNote);
    updateContour(tick, details);
  }

  private NoteProperties findHighestNote(Map<Integer, NoteProperties> groupNotes, long tick) {
    NoteProperties highestNote = null;
    for (NoteProperties tickEvent : groupNotes.values()) {
      if (highestNote == null || tickEvent.getMidiNote() > highestNote.getMidiNote()) {
        highestNote = tickEvent;
      }
    }
    return highestNote;
  }

  private void updateContour(long tick, Details details) {
    NoteProperties highestNote = findHighestNote(details.groupNotes, tick);
    if (highestNote != currentHighestNote) {
      if (currentHighestNote != null) {
        long duration = tick - lastTick;
        if (duration > Default.TICKS_PER_BEAT / 8) {
          details.getContour().add(new Contour(lastTick, currentHighestNote.getMidiNote(), duration));
        }
      }
      lastTick = tick;
      currentHighestNote = highestNote;
    }
  }

  private void updateUtilizationOnAdd(long tick, Details details) {
    int activeNoteCount = details.groupNotes.size();
    if (activeNoteCount == 0) {
      details.setGapTicks(details.getGapTicks() + tick - details.getPreviousTick());
    } else {
      details.setConcurrentTicks(details.getConcurrentTicks() + activeNoteCount * (tick - details.getPreviousTick()));
    }
    details.setPreviousTick(tick);
  }

  private void updateUtilizationOnRemove(long tick, Details details) {
    int activeNoteCount = details.groupNotes.size();
    details.setConcurrentTicks(details.getConcurrentTicks() + activeNoteCount * (tick - details.getPreviousTick()));
    details.setPreviousTick(tick);
  }
}
