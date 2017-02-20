package com.example.afs.musicpad.song;

import java.util.HashMap;

public class Group {

  public static class GroupNotes extends HashMap<Integer, NoteProperties> {
  }

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

  private GroupNotes[] channelGroupNotes = new GroupNotes[Midi.CHANNELS];

  public Group() {
    for (int i = 0; i < Midi.CHANNELS; i++) {
      channelGroupNotes[i] = new GroupNotes();
    }
  }

  public void add(long tick, int channel, int note, int velocity, int instrument) {
    NoteProperties noteProperties = new NoteProperties(tick, instrument, velocity);
    channelGroupNotes[channel].put(note, noteProperties);
  }

  public boolean allNotesAreOff(int channel) {
    return channelGroupNotes[channel].size() == 0;
  }

  public NoteProperties get(int channel, int note) {
    return channelGroupNotes[channel].get(note);
  }

  public GroupNotes getGroupNotes(int channel) {
    return channelGroupNotes[channel];
  }

  public void remove(long tick, int channel, int note) {
    channelGroupNotes[channel].remove(note);
  }

}
