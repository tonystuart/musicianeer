// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import com.example.afs.musicpad.analyzer.TranspositionFinder;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Note.NoteBuilder;
import com.example.afs.musicpad.util.Value;

public class Song {

  public class ChannelNoteIterator implements Iterator<Note>, Iterable<Note> {

    private int channel;
    private Note nextChannelNote;
    private Iterator<Note> iterator;

    public ChannelNoteIterator(int channel) {
      this.channel = channel;
      this.iterator = notes.iterator();
    }

    @Override
    public boolean hasNext() {
      loadNextChannelNote();
      return nextChannelNote != null;
    }

    @Override
    public Iterator<Note> iterator() {
      return this;
    }

    @Override
    public Note next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      Note note = nextChannelNote;
      nextChannelNote = null;
      return note;
    }

    private void loadNextChannelNote() {
      while (iterator.hasNext() && nextChannelNote == null) {
        Note note = iterator.next();
        if (note.getChannel() == channel) {
          nextChannelNote = note;
        }
      }
    }

  }

  private String title;
  private long duration;
  private int transposition;
  private TreeSet<Note> notes = new TreeSet<>();
  private TreeSet<Word> words = new TreeSet<>();
  private ChannelFacets channelFacets = new ChannelFacets();
  private Integer distanceToWhiteKeys;

  public Song() {
  }

  public Song(String name) {
    this.title = name;
  }

  public Song(String name, int transposition) {
    this.title = name;
    this.transposition = transposition;
  }

  public void add(Note note) {
    notes.add(note);
    channelFacets.add(note);
    long noteEnd = note.getTick() + note.getDuration();
    if (noteEnd > duration) {
      duration = noteEnd;
    }
  }

  public void add(Word word) {
    words.add(word);
  }

  public long append(Song newSong) {
    long lastTick = getLastTick();
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

  public int getAverageMidiNote(int channel) {
    return channelFacets.getFacet(channel).getAverageMidiNote();
  }

  public int getBeatsPerMeasure(long tick) {
    int beatsPerMeasure;
    Note note = getControllingNote(tick);
    if (note == null) {
      beatsPerMeasure = Default.BEATS_PER_MEASURE;
    } else {
      beatsPerMeasure = note.getBeatsPerMeasure();
    }
    return beatsPerMeasure;
  }

  public int getBeatsPerMinute(long tick) {
    int beatsPerMinute;
    Note note = getControllingNote(tick);
    if (note == null) {
      beatsPerMinute = Default.BEATS_PER_MINUTE;
    } else {
      beatsPerMinute = note.getBeatsPerMinute();
    }
    return beatsPerMinute;
  }

  public int getBeatUnit(long tick) {
    int beatUnit;
    Note note = getControllingNote(tick);
    if (note == null) {
      beatUnit = Default.BEAT_UNIT;
    } else {
      beatUnit = note.getBeatUnit();
    }
    return beatUnit;
  }

  public int getChannelNoteCount(int channel) {
    return channelFacets.getFacet(channel).getTotalNoteCount();
  }

  public Iterable<Note> getChannelNotes(int channel) {
    return new ChannelNoteIterator(channel);
  }

  public int[] getChromaticNoteCounts(int channel) {
    return channelFacets.getFacet(channel).getChromaticNoteCounts();
  }

  public int getConcurrency(int channel) {
    return channelFacets.getFacet(channel).getConcurrency();
  }

  public Note getControllingNote(long tick) {
    Note tickNote = new Note(tick);
    Note controllingNote = notes.floor(tickNote);
    if (controllingNote == null) {
      controllingNote = notes.ceiling(tickNote);
    }
    return controllingNote;
  }

  public Integer getDistanceToWhiteKeys() {
    if (distanceToWhiteKeys == null) {
      distanceToWhiteKeys = TranspositionFinder.getDistanceToWhiteKeys(this);
    }
    return distanceToWhiteKeys;
  }

  public int[] getDistinctNoteCount(int channel) {
    return channelFacets.getFacet(channel).getDistinctNoteCounts();
  }

  public long getDuration() {
    return duration;
  }

  public int getHighestMidiNote() {
    int highest;
    if (notes.size() == 0) {
      highest = -1;
    } else {
      highest = 0;
      for (int channel = 0; channel < Midi.CHANNELS; channel++) {
        int channelHighest = getHighestMidiNote(channel);
        if (channelHighest > highest) {
          highest = channelHighest;
        }
      }
    }
    return highest;
  }

  public int getHighestMidiNote(int channel) {
    return channelFacets.getFacet(channel).getHighestMidiNote();
  }

  public long getLastTick() {
    long length;
    if (notes.size() == 0) {
      length = 0;
    } else {
      length = notes.last().getTick();
    }
    return length;
  }

  public int getLowestMidiNote() {
    int lowest;
    if (notes.size() == 0) {
      lowest = -1;
    } else {
      lowest = Midi.MAX_VALUE;
      for (int channel = 0; channel < Midi.CHANNELS; channel++) {
        int channelLowest = getLowestMidiNote(channel);
        if (channelLowest != -1 && channelLowest < lowest) {
          lowest = channelLowest;
        }
      }
    }
    return lowest;
  }

  public int getLowestMidiNote(int channel) {
    return channelFacets.getFacet(channel).getLowestMidiNote();
  }

  public TreeSet<Note> getNotes() {
    return notes;
  }

  public TreeSet<Note> getNotes(int channel) {
    TreeSet<Note> channelNotes = new TreeSet<>();
    for (Note note : notes) {
      if (note.getChannel() == channel) {
        channelNotes.add(note);
      }
    }
    return channelNotes;
  }

  public NavigableSet<Note> getNotes(long fromTick, long toTick) {
    Note firstNote = new Note(fromTick);
    Note lastNote = new Note(toTick);
    NavigableSet<Note> set = notes.subSet(firstNote, true, lastNote, false);
    return set;
  }

  public int getOccupancy(int channel) {
    return channelFacets.getFacet(channel).getOccupancy();
  }

  public List<String> getProgramNames(int channel) {
    List<String> programNames = new LinkedList<>();
    Set<Integer> programs = channelFacets.getFacet(channel).getPrograms();
    for (Integer program : programs) {
      String programName = Instruments.getProgramName(program) + " (" + Value.toNumber(program) + ")";
      programNames.add(programName);
    }
    return programNames;
  }

  public Set<Integer> getPrograms(int channel) {
    Set<Integer> programs;
    if (channel == Midi.MELODIC) {
      programs = Collections.emptySet();
    } else {
      programs = channelFacets.getFacet(channel).getPrograms();
    }
    return programs;
  }

  public int getTicksPerMeasure(long tick) {
    int ticksPerMeasure = getBeatsPerMeasure(tick) * Default.TICKS_PER_BEAT;
    return ticksPerMeasure;
  }

  public String getTitle() {
    return title;
  }

  public int getTransposition() {
    return transposition;
  }

  public TreeSet<Word> getWords() {
    return words;
  }

  public long roundTickToNextMeasure(long tick) {
    int ticksPerMeasure = getTicksPerMeasure(tick);
    return ((tick + (ticksPerMeasure - 1)) / ticksPerMeasure) * ticksPerMeasure;
  }

  public long roundTickToThisMeasure(long tick) {
    int ticksPerMeasure = getTicksPerMeasure(tick);
    return (tick / ticksPerMeasure) * ticksPerMeasure;
  }

  public void setConcurrency(int channel, int concurrency) {
    channelFacets.getFacet(channel).setConcurrency(concurrency);
  }

  public void setOccupancy(int channel, int occupancy) {
    channelFacets.getFacet(channel).setOccupancy(occupancy);
  }

  @Override
  public String toString() {
    return "Song [name=" + title + ", transposition=" + transposition + "]";
  }

  public void transposeBy(int distance) {
    if (distance != 0) {
      this.transposition += distance;
      ChannelFacets newChannelFacets = new ChannelFacets();
      for (int channel = 0; channel < Midi.CHANNELS; channel++) {
        Facet channelFacet = channelFacets.getFacet(channel);
        Facet newChannelFacet = newChannelFacets.getFacet(channel);
        newChannelFacet.setConcurrency(channelFacet.getConcurrency());
        newChannelFacet.setOccupancy(channelFacet.getOccupancy());
      }
      for (Note note : notes) {
        int channel = note.getChannel();
        if (channel != Midi.DRUM) {
          note.transpose(distance);
        }
        newChannelFacets.add(note);
      }
      channelFacets = newChannelFacets;
    }
  }

  public void transposeTo(int desiredDistance) {
    int distance = desiredDistance - this.transposition;
    transposeBy(distance);
  }

}
