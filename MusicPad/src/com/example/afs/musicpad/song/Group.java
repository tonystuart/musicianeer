package com.example.afs.musicpad.song;

import java.util.HashMap;
import java.util.Map;

public class Group {

  public static class NoteProperties {
    private long tick;
    private int instrument;
    private int velocity;

    public NoteProperties(long tick, int instrument, int velocity) {
      this.tick = tick;
      this.instrument = instrument;
      this.velocity = velocity;
    }

    public int getInstrument() {
      return instrument;
    }

    public long getTick() {
      return tick;
    }

    public int getVelocity() {
      return velocity;
    }
  }

  private static class Details {
    private long gapTicks;
    private long previousTick;
    private long concurrentTicks;
    private Map<Integer, NoteProperties> groupNotes = new HashMap<>();
  }

  private Details[] channelDetails = new Details[Midi.CHANNELS];

  public Group() {
    for (int i = 0; i < Midi.CHANNELS; i++) {
      channelDetails[i] = new Details();
    }
  }

  public void add(long tick, int channel, int note, int velocity, int instrument) {
    Details details = channelDetails[channel];
    NoteProperties noteProperties = new NoteProperties(tick, instrument, velocity);

    int activeNoteCount = details.groupNotes.size();
    if (activeNoteCount == 0) {
      details.gapTicks += tick - details.previousTick;
    } else {
      details.concurrentTicks += activeNoteCount * (tick - details.previousTick);
    }
    details.previousTick = tick;

    details.groupNotes.put(note, noteProperties);
  }

  public boolean allNotesAreOff(int channel) {
    return channelDetails[channel].groupNotes.size() == 0;
  }

  public NoteProperties get(int channel, int note) {
    return channelDetails[channel].groupNotes.get(note);
  }

  public int getOccupancy(int channel) {
    int occupancy = 0;
    Details details = channelDetails[channel];
    long totalTicks = details.previousTick;
    if (totalTicks != 0) {
      long occupancyTicks = totalTicks - details.gapTicks;
      occupancy = (int) ((occupancyTicks * 100) / totalTicks);
    }
    return occupancy;
  }

  public int getConcurrency(int channel) {
    int concurrency = 0;
    Details details = channelDetails[channel];
    long totalTicks = details.previousTick;
    long occupancyTicks = totalTicks - details.gapTicks;
    if (occupancyTicks != 0) {
      concurrency = (int) ((details.concurrentTicks * 100) / occupancyTicks);
    }
    return concurrency;
  }

  public void remove(long tick, int channel, int note) {
    Details details = channelDetails[channel];

    int activeNoteCount = details.groupNotes.size();
    details.concurrentTicks += activeNoteCount * (tick - details.previousTick);
    details.previousTick = tick;

    details.groupNotes.remove(note);
  }
}
