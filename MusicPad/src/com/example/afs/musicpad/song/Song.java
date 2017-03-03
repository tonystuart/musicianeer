// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Note.NoteBuilder;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Song {

  private String name;
  private TreeSet<Note> notes = new TreeSet<>();
  private RandomAccessList<Line> lines = new DirectList<>();
  private ChannelFacets channelFacets = new ChannelFacets();

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
    Facet facet = channelFacets.getFacet(channel);
    facet.countNote(midiNote);
    if (channel == Midi.DRUM) {
      facet.addProgram(midiNote);
    } else {
      facet.addProgram(program);
    }
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

  public int getChannelNoteCount(int channel) {
    return channelFacets.getFacet(channel).getTotalNoteCount();
  }

  public int[] getCommonNoteCounts(int channel) {
    return channelFacets.getFacet(channel).getCommonNoteCounts();
  };

  public int getConcurrency(int channel) {
    return channelFacets.getFacet(channel).getConcurrency();
  };

  public TreeSet<Contour> getContours(int channel) {
    return channelFacets.getFacet(channel).getContour();
  };

  public int[] getDistinctNoteCount(int channel) {
    return channelFacets.getFacet(channel).getDistinctNoteCounts();
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

  public int getOccupancy(int channel) {
    return channelFacets.getFacet(channel).getOccupancy();
  };

  public List<String> getProgramNames(int channel) {
    List<String> programNames = new LinkedList<>();
    Set<Integer> programs = channelFacets.getFacet(channel).getPrograms();
    for (Integer program : programs) {
      String programName = Instruments.getInstrumentName(program);
      programNames.add(programName);
    }
    return programNames;
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

  public long roundTickToNextMeasure(long tick) {
    int ticksPerMeasure = getTicksPerMeasure(tick);
    return ((tick + (ticksPerMeasure - 1)) / ticksPerMeasure) * ticksPerMeasure;
  };

  public long roundTickToThisMeasure(long tick) {
    int ticksPerMeasure = getTicksPerMeasure(tick);
    return (tick / ticksPerMeasure) * ticksPerMeasure;
  };

  public void setConcurrency(int channel, int concurrency) {
    channelFacets.getFacet(channel).setConcurrency(concurrency);
  };

  public void setContour(int channel, TreeSet<Contour> contour) {
    channelFacets.getFacet(channel).setContour(contour);
  }

  public void setOccupancy(int channel, int occupancy) {
    channelFacets.getFacet(channel).setOccupancy(occupancy);
  }

  @Override
  public String toString() {
    return "Song [name=" + name + "]";
  };
}
