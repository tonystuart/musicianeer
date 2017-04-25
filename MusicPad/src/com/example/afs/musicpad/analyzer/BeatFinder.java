// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.DirectList;

public class BeatFinder {

  public static class ActiveNote {
    private BeatNote beatNote;
    private long until;

    public ActiveNote(BeatNote beatNote, long until) {
      this.beatNote = beatNote;
      this.until = until;
    }

    public BeatNote getBeatNote() {
      return beatNote;
    }

    public long getUntil() {
      return until;
    }

    @Override
    public String toString() {
      return "ActiveNote [beatNote=" + beatNote + ", until=" + until + "]";
    }
  }

  public static class ActiveNotes extends HashSet<ActiveNote> {

  }

  public static class Beat {
    private BeatNotes beatNotes = new BeatNotes();

    public Beat(ActiveNotes activeNotes) {
      for (ActiveNote activeNote : activeNotes) {
        beatNotes.add(activeNote.getBeatNote());
      }
    }

    public void add(BeatNote beatNote) {
      beatNotes.add(beatNote);
    }

    public BeatNotes getBeatNotes() {
      return beatNotes;
    }

    @Override
    public String toString() {
      return "Beat [beatNotes=" + beatNotes + "]";
    }
  }

  public static class BeatNote implements Comparable<BeatNote> {
    private int commonNote;

    public BeatNote(int commonNote) {
      this.commonNote = commonNote;
    }

    @Override
    public int compareTo(BeatNote that) {
      return this.commonNote - that.commonNote;
    }

    @Override
    public boolean equals(Object that) {
      if (this == that) {
        return true;
      }
      if (that == null) {
        return false;
      }
      if (getClass() != that.getClass()) {
        return false;
      }
      BeatNote other = (BeatNote) that;
      if (commonNote != other.commonNote) {
        return false;
      }
      return true;
    }

    public int getCommonNote() {
      return commonNote;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + commonNote;
      return result;
    }

    @Override
    public String toString() {
      return "BeatNote [commonNote=" + commonNote + "]";
    }

  }

  public static class BeatNotes extends TreeSet<BeatNote> implements Comparable<BeatNotes> {

    @Override
    public int compareTo(BeatNotes that) {
      int relationship = 0;
      Iterator<BeatNote> thisIterator = this.iterator();
      Iterator<BeatNote> thatIterator = that.iterator();
      while (thisIterator.hasNext() && thatIterator.hasNext() && relationship == 0) {
        BeatNote thisBeatNote = thisIterator.next();
        BeatNote thatBeatNote = thatIterator.next();
        relationship = thisBeatNote.compareTo(thatBeatNote);
      }
      if (relationship == 0) {
        if (thatIterator.hasNext()) {
          relationship = -1;
        } else if (thisIterator.hasNext()) {
          relationship = 1;
        }
      }
      return relationship;
    }

    @Override
    public String toString() {
      return Names.formatBeatNotes(this);
    }
  }

  public static class Beats extends DirectList<Beat> {

  }

  public Beats findBeats(NavigableSet<Note> notes) {
    Beat beat = null;
    Beats beats = new Beats();
    ActiveNotes activeNotes = new ActiveNotes();
    int lastBeatIndex = -1;
    for (Note note : notes) {
      if (note.getChannel() != Midi.DRUM) {
        long tick = note.getTick();
        tick = tick + Default.GAP_BEAT_UNIT;
        int beatIndex = (int) (tick / Default.TICKS_PER_BEAT);
        while (lastBeatIndex < beatIndex) {
          System.out.println(((lastBeatIndex * Default.TICKS_PER_BEAT) / note.getTicksPerMeasure()) + " " + lastBeatIndex + " " + beat);
          beat = new Beat(activeNotes);
          beats.add(beat);
          lastBeatIndex++;
          long lastTick = lastBeatIndex * Default.TICKS_PER_BEAT;
          Iterator<ActiveNote> iterator = activeNotes.iterator();
          while (iterator.hasNext()) {
            ActiveNote activeNote = iterator.next();
            if (activeNote.getUntil() < lastTick) {
              iterator.remove();
            }
          }
        }
        int midiNote = note.getMidiNote();
        int commonNote = midiNote % Midi.SEMITONES_PER_OCTAVE;
        BeatNote beatNote = new BeatNote(commonNote);
        beat.add(beatNote);
        long until = note.getTick() + note.getDuration();
        until = until - Default.GAP_BEAT_UNIT;
        activeNotes.add(new ActiveNote(beatNote, until));
      }
    }
    return beats;
  }
}
