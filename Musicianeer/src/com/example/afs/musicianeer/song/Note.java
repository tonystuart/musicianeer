// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.song;

import com.example.afs.musicianeer.analyzer.Names;
import com.example.afs.musicianeer.midi.Midi;

public class Note extends Item<Note> {

  public static class NoteBuilder {
    private long tick;
    private int channel;
    private int midiNote;
    private int velocity;
    private long duration;
    private int program;
    private int startIndex;
    private int endIndex;
    private int beatsPerMinute = Default.BEATS_PER_MINUTE;
    private int beatsPerMeasure = Default.BEATS_PER_MEASURE;
    private int beatUnit = Default.BEAT_UNIT;

    public Note create() {
      return new Note(tick, channel, midiNote, velocity, duration, program, startIndex, endIndex, beatsPerMinute, beatsPerMeasure, beatUnit);
    }

    public NoteBuilder withBeatsPerMeasure(int beatsPerMeasure) {
      this.beatsPerMeasure = beatsPerMeasure;
      return this;
    }

    public NoteBuilder withBeatsPerMinute(int beatsPerMinute) {
      this.beatsPerMinute = beatsPerMinute;
      return this;
    }

    public NoteBuilder withBeatUnit(int beatUnit) {
      this.beatUnit = beatUnit;
      return this;
    }

    public NoteBuilder withChannel(int channel) {
      this.channel = channel;
      return this;
    }

    public NoteBuilder withDuration(int duration) {
      this.duration = duration;
      return this;
    }

    public NoteBuilder withEndIndex(int endIndex) {
      this.endIndex = endIndex;
      return this;
    }

    public NoteBuilder withMidiNote(int midiNote) {
      this.midiNote = midiNote;
      return this;
    }

    public NoteBuilder withNote(Note note) {
      tick = note.getTick();
      channel = note.getChannel();
      midiNote = note.getMidiNote();
      velocity = note.getVelocity();
      duration = note.getDuration();
      program = note.getProgram();
      startIndex = note.getStartIndex();
      endIndex = note.getEndIndex();
      beatsPerMinute = note.getBeatsPerMinute();
      beatsPerMeasure = note.getBeatsPerMeasure();
      beatUnit = note.getBeatUnit();
      return this;
    }

    public NoteBuilder withProgram(int program) {
      this.program = program;
      return this;
    }

    public NoteBuilder withStartIndex(int startIndex) {
      this.startIndex = startIndex;
      return this;
    }

    public NoteBuilder withTick(long tick) {
      this.tick = tick;
      return this;
    }

    public NoteBuilder withVelocity(int velocity) {
      this.velocity = velocity;
      return this;
    }
  }

  private int channel;
  private int midiNote;
  private int velocity;
  private long duration;
  private int program;
  private int startIndex;
  private int endIndex;
  private int beatsPerMinute;
  private int beatsPerMeasure;
  private int beatUnit;

  public Note(long tick) {
    super(tick);
  }

  public Note(long tick, int channel, int midiNote, int velocity, long duration, int program, int startIndex, int endIndex, int beatsPerMinute, int beatsPerMeasure, int beatUnit) {
    super(tick);
    this.channel = channel;
    this.midiNote = midiNote;
    this.velocity = velocity;
    this.duration = duration;
    this.program = program;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.beatsPerMinute = beatsPerMinute;
    this.beatsPerMeasure = beatsPerMeasure;
    this.beatUnit = beatUnit;
  }

  @Override
  public int compareTo(Note that) {
    int deltaTick = (int) (this.tick - that.tick);
    if (deltaTick != 0) {
      return deltaTick;
    }
    int deltaChannel = this.channel - that.channel;
    if (deltaChannel != 0) {
      return deltaChannel;
    }
    int deltaNote = this.midiNote - that.midiNote;
    if (deltaNote != 0) {
      return deltaNote;
    }
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Note other = (Note) obj;
    if (channel != other.channel) {
      return false;
    }
    if (midiNote != other.midiNote) {
      return false;
    }
    if (tick != other.tick) {
      return false;
    }
    return true;
  }

  public int getBeatsPerMeasure() {
    return beatsPerMeasure;
  }

  public int getBeatsPerMinute() {
    return beatsPerMinute;
  }

  public int getBeatUnit() {
    return beatUnit;
  }

  public int getChannel() {
    return channel;
  }

  public long getDuration() {
    return duration;
  }

  public int getEndIndex() {
    return endIndex;
  }

  public int getMeasure() {
    return (int) (tick / getTicksPerMeasure());
  }

  public int getMidiNote() {
    return midiNote;
  }

  public int getProgram() {
    return program;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public long getTickOfNextMeasure() {
    int ticksPerMeasure = getTicksPerMeasure();
    return ((tick + ticksPerMeasure) / ticksPerMeasure) * ticksPerMeasure;
  }

  public long getTickOfThisMeasure() {
    int ticksPerMeasure = getTicksPerMeasure();
    return (tick / ticksPerMeasure) * ticksPerMeasure;
  }

  public int getTicksPerMeasure() {
    return beatsPerMeasure * Default.TICKS_PER_BEAT;
  }

  public int getVelocity() {
    return velocity;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + channel;
    result = prime * result + midiNote;
    result = prime * result + (int) (tick ^ (tick >>> 32));
    return result;
  }

  public long roundTickToNextBeat() {
    return ((tick + (Default.TICKS_PER_BEAT - 1)) / Default.TICKS_PER_BEAT) * Default.TICKS_PER_BEAT;
  }

  public long roundTickToNextMeasure() {
    int ticksPerMeasure = getTicksPerMeasure();
    return ((tick + (ticksPerMeasure - 1)) / ticksPerMeasure) * ticksPerMeasure;
  }

  public long roundTickToThisMeasure() {
    int ticksPerMeasure = getTicksPerMeasure();
    return (tick / ticksPerMeasure) * ticksPerMeasure;
  }

  @Override
  public String toString() {
    String noteDescription = getNoteDescription();
    return "Note [tick=" + tick + ", channel=" + channel + ", midiNote=" + midiNote + ", velocity=" + velocity + ", duration=" + duration + ", instrument=" + program + ", bpm=" + beatsPerMinute + ", time=" + beatsPerMeasure + "/" + beatUnit + " " + noteDescription + "]";
  }

  public void transpose(int distance) {
    midiNote += distance;
  }

  private String getNoteDescription() {
    String noteDescription;
    if (channel == Midi.DRUM) {
      noteDescription = Names.formatDrumName(midiNote);
    } else {
      noteDescription = Names.formatNoteName(midiNote);
    }
    return noteDescription;
  }
}
