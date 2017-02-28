// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.example.afs.musicpad.song.Note.NoteBuilder;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Song {

  private String name;
  private TreeSet<Note> notes = new TreeSet<>();
  private RandomAccessList<Line> lines = new DirectList<>();
  private ChannelPrograms channelPrograms = new ChannelPrograms();
  // TODO: Move these to a new class, e.g. Details
  private int[] occupancy = new int[Midi.CHANNELS];
  private int[] concurrency = new int[Midi.CHANNELS];
  private int[] channelNoteCount = new int[Midi.CHANNELS];
  private int[][] commonNoteCount = new int[Midi.CHANNELS][Midi.SEMITONES_PER_OCTAVE];
  private int[][] distinctNoteCount = new int[Midi.CHANNELS][Midi.NOTES];
  private int modificationCount;
  private RandomAccessList<TreeSet<Contour>> contours = new DirectList<>(Midi.CHANNELS);

  public Song() {
  }

  public Song(String name) {
    this.name = name;
  }

  public void add(Line line) {
    lines.add(line);
  }

  public void add(Note note) {
    notes.add(note);
    int channel = note.getChannel();
    int program = note.getProgram();
    int midiNote = note.getMidiNote();
    channelNoteCount[channel]++;
    channelPrograms.save(channel, program);
    commonNoteCount[channel][midiNote % Midi.SEMITONES_PER_OCTAVE]++;
    distinctNoteCount[channel][midiNote]++;
    modificationCount++;
  }

  public long append(Song newSong) {
    long lastTick = getLength();
    long appendTick = roundTickToNextMeasure(lastTick);
    if (newSong.notes.size() > 0) {
      long baseTick = newSong.notes.first().getTick();
      for (Note oldNote : newSong.getNotes()) {
        long tick = appendTick + oldNote.getTick() - baseTick;
        Note newNote = new NoteBuilder().withNote(oldNote).withTick(tick).create();
        add(newNote);
      }
    }
    return appendTick;
  }

  public int[] getChannelNoteCount() {
    return channelNoteCount;
  }

  public ChannelPrograms getChannelPrograms() {
    return channelPrograms;
  }

  public int[][] getCommonNoteCount() {
    return commonNoteCount;
  };

  public int[] getConcurrency() {
    return concurrency;
  };

  public TreeSet<Contour> getContours(int channel) {
    return contours.get(channel);
  };

  public int[][] getDistinctNoteCount() {
    return distinctNoteCount;
  };

  public long getLength() {
    long length;
    if (notes.size() == 0) {
      length = 0;
    } else {
      length = notes.last().getTick();
    }
    return length;
  };

  public RandomAccessList<Line> getLines() {
    return lines;
  };

  public int getModificationCount() {
    return modificationCount;
  };

  public String getName() {
    return name;
  };

  public TreeSet<Note> getNotes() {
    return notes;
  };

  public NavigableSet<Note> getNotes(long fromTick, long toTick) {
    Note firstNote = new Note(fromTick);
    Note lastNote = new Note(toTick);
    NavigableSet<Note> set = notes.subSet(firstNote, true, lastNote, false);
    return set;
  };

  public int[] getOccupancy() {
    return occupancy;
  };

  public List<String> getProgramNames(int channel) {
    return channelPrograms.getProgramNames(channel);
  };

  public int getTicksPerMeasure(long tick) {
    int ticksPerMeasure;
    Note priorNote = notes.floor(new Note(tick));
    if (priorNote == null) {
      ticksPerMeasure = Default.BEATS_PER_MEASURE * Default.TICKS_PER_BEAT;
    } else {
      ticksPerMeasure = priorNote.getTicksPerMeasure();
    }
    return ticksPerMeasure;
  };

  public void resetModificationCount() {
    modificationCount = 0;
  };

  public long roundTickToNextMeasure(long tick) {
    int ticksPerMeasure = getTicksPerMeasure(tick);
    return ((tick + (ticksPerMeasure - 1)) / ticksPerMeasure) * ticksPerMeasure;
  };

  public long roundTickToThisMeasure(long tick) {
    int ticksPerMeasure = getTicksPerMeasure(tick);
    return (tick / ticksPerMeasure) * ticksPerMeasure;
  };

  public void setConcurrency(int channel, int concurrency) {
    this.concurrency[channel] = concurrency;
  };

  public void setContour(int channel, TreeSet<Contour> contour) {
    while (contours.size() <= channel) {
      contours.add(null);
    }
    this.contours.set(channel, contour);
  }

  public void setOccupancy(int channel, int occupancy) {
    this.occupancy[channel] = occupancy;
  }

  @Override
  public String toString() {
    return "Song [name=" + name + ", channelNoteCount=" + Arrays.toString(channelNoteCount) + "]";
  };
}
