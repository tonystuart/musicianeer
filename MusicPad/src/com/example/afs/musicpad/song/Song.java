// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.example.afs.musicpad.analyzer.TranspositionFinder;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Note.NoteBuilder;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.FileUtilities;
import com.example.afs.musicpad.util.RandomAccessList;

public class Song {

  public interface DrumFormatter {
    String format(int drum);
  }

  public interface ProgramFormatter {
    String format(int program);
  }

  private File file;
  private String title;
  private long duration;
  private int transposition;

  private Integer distanceToWhiteKeys;
  private TreeSet<Note> notes = new TreeSet<>();
  private TreeSet<Word> words = new TreeSet<>();
  private ChannelFacets channelFacets = new ChannelFacets();

  private DrumFormatter drumFormatter = drum -> Instruments.getDrumName(drum);
  private ProgramFormatter programFormatter = program -> Instruments.getProgramName(program);

  public Song() {
  }

  public Song(File file) {
    this.file = file;
    this.title = FileUtilities.getBaseName(file.getPath());
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
    Song other = (Song) obj;
    if (title == null) {
      if (other.title != null) {
        return false;
      }
    } else if (!title.equals(other.title)) {
      return false;
    }
    return true;
  }

  public int getActiveChannelCount() {
    int activeChannelCount = 0;
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (getChannelNoteCount(channel) > 0) {
        activeChannelCount++;
      }
    }
    return activeChannelCount;
  }

  public int[] getActiveChannels() {
    int index = 0;
    int activeChannelCount = getActiveChannelCount();
    int[] activeChannels = new int[activeChannelCount];
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (getChannelNoteCount(channel) > 0) {
        activeChannels[index++] = channel;
      }
    }
    return activeChannels;
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

  public int getChannelEndCount(int channel) {
    return channelFacets.getFacet(channel).getEndCount();
  }

  public int getChannelNoteCount(int channel) {
    return channelFacets.getFacet(channel).getTotalNoteCount();
  }

  public Iterable<Note> getChannelNotes(int channel) {
    return new ChannelNotes(notes, channel);
  }

  public int getChannelStartCount(int channel) {
    return channelFacets.getFacet(channel).getStartCount();
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

  public File getFile() {
    return file;
  }

  public int getHighestMidiNote() {
    int highestMidiNote = Integer.MIN_VALUE;
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (channel != Midi.DRUM && getChannelNoteCount(channel) > 0) {
        int channelHighest = getHighestMidiNote(channel);
        highestMidiNote = Math.max(highestMidiNote, channelHighest);
      }
    }
    if (highestMidiNote == Integer.MIN_VALUE) {
      highestMidiNote = -1;
    }
    return highestMidiNote;
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
    int lowestMidiNote = Integer.MAX_VALUE;
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (channel != Midi.DRUM && getChannelNoteCount(channel) > 0) {
        int channelLowest = getLowestMidiNote(channel);
        lowestMidiNote = Math.min(lowestMidiNote, channelLowest);
      }
    }
    if (lowestMidiNote == Integer.MAX_VALUE) {
      lowestMidiNote = -1;
    }
    return lowestMidiNote;
  }

  public int getLowestMidiNote(int channel) {
    return channelFacets.getFacet(channel).getLowestMidiNote();
  }

  public int getMaximumTransposition() {
    int highestMidiNote = getHighestMidiNote();
    int maximumTransposition = (Midi.MAX_VALUE - highestMidiNote) + transposition;
    return maximumTransposition;
  }

  public int getMinimumTransposition() {
    int lowestMidiNote = getLowestMidiNote();
    int minimumTransposition = transposition - lowestMidiNote;
    return minimumTransposition;
  }

  public int getNoteCount() {
    return notes.size();
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

  public int getPercentMelody(int channel) {
    int melodyNoteCount = 0;
    for (Word word : words) {
      long tick = word.getTick();
      Note fromNote = new Note(tick - Default.GAP_BEAT_UNIT);
      Note toNote = new Note(tick + Default.GAP_BEAT_UNIT);
      SortedSet<Note> slice = notes.subSet(fromNote, toNote);
      for (Note note : slice) {
        if (note.getChannel() == channel) {
          melodyNoteCount++;
        }
      }
    }
    int channelNoteCount = getChannelNoteCount(channel);
    int percentMelody = (100 * melodyNoteCount) / channelNoteCount;
    return percentMelody;
  }

  public int getPresumedMelodyChannel() {

    final class ChannelMelodyScore implements Comparable<ChannelMelodyScore> {

      int channel;
      double score;

      ChannelMelodyScore(int channel) {
        this.channel = channel;
        int percentMelody = getPercentMelody(channel);
        int concurrency = getConcurrency(channel);
        int occupancy = getOccupancy(channel);
        // A perfect score is 1.0:
        // percentMelody = 100 (goes down as percentMelody goes down)
        // concurrency = 100 (goes down as concurrency goes up)
        // occupancy = 100 (goes down as occupancy goes down);
        score = percentMelody / 100d;
        score *= 100d / concurrency;
        score *= occupancy / 100d;
      }

      @Override
      public int compareTo(ChannelMelodyScore that) {
        // Invert the comparison so the highest score is first
        return this.score < that.score ? +1 : this.score > that.score ? -1 : 0;
      }

      @Override
      public String toString() {
        return "ChannelMelodyScorer [channel=" + channel + ", score=" + score + "]";
      }

    }

    RandomAccessList<ChannelMelodyScore> scores = new DirectList<>();
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (channel != Midi.DRUM && getChannelNoteCount(channel) > 0) {
        scores.add(new ChannelMelodyScore(channel));
      }
    }
    int presumedMelodyChannel;
    if (scores.size() > 0) {
      Collections.sort(scores);
      presumedMelodyChannel = scores.get(0).channel;
    } else {
      presumedMelodyChannel = -1;
    }
    return presumedMelodyChannel;
  }

  public List<String> getProgramNames(int channel) {
    return getProgramNames(channel, programFormatter, drumFormatter);
  }

  public List<String> getProgramNames(int channel, ProgramFormatter programFormatter, DrumFormatter drumFormatter) {
    List<String> programNames = new LinkedList<>();
    if (channel == Midi.DRUM) {
      int[] noteCounts = channelFacets.getFacet(channel).getDistinctNoteCounts();
      for (int midiNote = 0; midiNote < Midi.NOTES; midiNote++) {
        if (noteCounts[midiNote] > 0) {
          String programName = drumFormatter.format(midiNote);
          programNames.add(programName);
        }
      }
    } else {
      Set<Integer> programs = channelFacets.getFacet(channel).getPrograms();
      for (Integer program : programs) {
        String programName = programFormatter.format(program);
        programNames.add(programName);
      }
    }
    return programNames;
  }

  public Set<Integer> getPrograms(int channel) {
    Set<Integer> programs;
    programs = channelFacets.getFacet(channel).getPrograms();
    return programs;
  }

  public int getSeconds() {
    long tickDuration = getDuration();
    long beatDuration = tickDuration / Default.TICKS_PER_BEAT;
    int beatsPerMinute = getBeatsPerMinute(0);
    int seconds = (int) ((60 * beatDuration) / beatsPerMinute);
    return seconds;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
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

  public void setEndCount(int channel, int endCount) {
    channelFacets.getFacet(channel).setEndCount(endCount);
  }

  public void setOccupancy(int channel, int occupancy) {
    channelFacets.getFacet(channel).setOccupancy(occupancy);
  }

  public void setStartCount(int channel, int startCount) {
    channelFacets.getFacet(channel).setStartCount(startCount);
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
      distanceToWhiteKeys = null;
    }
  }

  public void transposeTo(int desiredDistance) {
    int distance = desiredDistance - this.transposition;
    transposeBy(distance);
  }

}
