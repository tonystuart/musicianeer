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
    private NoteProperties currentHighestNote;
    private TreeSet<Contour> contour = new TreeSet<>();
    private Map<Integer, NoteProperties> groupNotes = new HashMap<>();

    public long getConcurrentTicks() {
      return concurrentTicks;
    }

    public TreeSet<Contour> getContour() {
      return contour;
    }

    public NoteProperties getCurrentHighestNote() {
      return currentHighestNote;
    }

    public long getGapTicks() {
      return gapTicks;
    }

    public Map<Integer, NoteProperties> getGroupNotes() {
      return groupNotes;
    }

    public long getPreviousTick() {
      return previousTick;
    }

    public void setConcurrentTicks(long concurrentTicks) {
      this.concurrentTicks = concurrentTicks;
    }

    public void setCurrentHighestNote(NoteProperties currentHighestNote) {
      this.currentHighestNote = currentHighestNote;
    }

    public void setGapTicks(long gapTicks) {
      this.gapTicks = gapTicks;
    }

    public void setPreviousTick(long previousTick) {
      this.previousTick = previousTick;
    }
  }

  private Details[] channelDetails = new Details[Midi.CHANNELS];

  public Group() {
    for (int i = 0; i < Midi.CHANNELS; i++) {
      channelDetails[i] = new Details();
    }
  }

  public void add(long tick, int channel, int midiNote, int velocity, int instrument) {
    Details details = channelDetails[channel];
    NoteProperties noteProperties = new NoteProperties(tick, midiNote, instrument, velocity);
    updateUtilizationOnAdd(tick, details);
    details.getGroupNotes().put(midiNote, noteProperties);
    updateContour(tick, details);
    details.setPreviousTick(tick);
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
    details.getGroupNotes().remove(midiNote);
    updateContour(tick, details);
    details.setPreviousTick(tick);
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
    NoteProperties highestNote = findHighestNote(details.getGroupNotes(), tick);
    if (highestNote != details.getCurrentHighestNote()) {
      if (details.getCurrentHighestNote() != null) {
        long duration = tick - details.getPreviousTick();
        if (duration > Default.TICKS_PER_BEAT / 8) {
          details.getContour().add(new Contour(details.getPreviousTick(), details.getCurrentHighestNote().getMidiNote(), duration));
        }
      }
      details.setCurrentHighestNote(highestNote);
    }
  }

  private void updateUtilizationOnAdd(long tick, Details details) {
    int activeNoteCount = details.getGroupNotes().size();
    if (activeNoteCount == 0) {
      details.setGapTicks(details.getGapTicks() + tick - details.getPreviousTick());
    } else {
      details.setConcurrentTicks(details.getConcurrentTicks() + activeNoteCount * (tick - details.getPreviousTick()));
    }
  }

  private void updateUtilizationOnRemove(long tick, Details details) {
    int activeNoteCount = details.getGroupNotes().size();
    details.setConcurrentTicks(details.getConcurrentTicks() + activeNoteCount * (tick - details.getPreviousTick()));
  }
}
