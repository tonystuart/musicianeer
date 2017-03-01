// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Item;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class ChordFinder {

  public static class Chord extends Item<Chord> {
    private ChordType chordType;
    private long duration;

    public Chord(ChordType chordType, long tick, long duration) {
      super(tick);
      this.chordType = chordType;
      this.duration = duration;
    }

    public Chord(long tick) {
      super(tick);
    }

    public ChordType getChordType() {
      return chordType;
    }

    public long getDuration() {
      return duration;
    }

    @Override
    public long getTick() {
      return tick;
    }

    @Override
    public String toString() {
      return "ChordInstance [chordType=" + chordType + ", tick=" + tick + ", duration=" + duration + "]";
    }
  }

  public static class ChordIntervals {
    private String name;
    private int[] intervals;

    public ChordIntervals(String name, int[] intervals) {
      this.name = name;
      this.intervals = intervals;
    }

    public int[] getIntervals() {
      return intervals;
    }

    public String getName() {
      return name;
    }

  }

  public static class ChordType {
    private String name;
    private int[] semitones;

    public ChordType(int root, ChordIntervals chordIntervals) {
      name = Names.getNoteName(root) + chordIntervals.getName();
      int[] intervals = chordIntervals.getIntervals();
      semitones = new int[intervals.length];
      int semitone = root;
      for (int i = 0; i < semitones.length; i++) {
        semitone = root + intervals[i];
        semitones[i] = semitone;
      }
    }

    public int getLength() {
      return semitones.length;
    }

    public String getName() {
      return name;
    }

    public int[] getSemitones() {
      return semitones;
    }

    public int match(RandomAccessList<Note> notes, int noteIndex) {
      int gap = 0;
      long maxTick = 0;
      long nextMeasureTick = 0;
      int chordIndex = 0;
      boolean isMatch = true;
      boolean[] matches = new boolean[semitones.length];
      while ((noteIndex + chordIndex) < notes.size() && isMatch) {
        Note note = notes.get(noteIndex + chordIndex);
        long tick = note.getTick();
        long duration = note.getDuration();
        if (chordIndex == 0) {
          int ticksPerMeasure = note.getTicksPerMeasure();
          long endingTick = tick + duration;
          nextMeasureTick = ((endingTick + ticksPerMeasure) / ticksPerMeasure) * ticksPerMeasure;
          gap = ticksPerMeasure / Default.GAP_BEAT_UNIT;
          maxTick = tick + duration;
        }
        if ((maxTick + gap) < tick || tick > (nextMeasureTick - gap) || !contains(note.getMidiNote(), matches)) {
          isMatch = false;
        } else {
          isMatch = true;
          maxTick = Math.max(maxTick, tick + duration);
          chordIndex++;
        }
      }
      return allSet(matches) ? chordIndex : 0;
    }

    @Override
    public String toString() {
      return "Chord [name=" + name + ", semitones=" + Arrays.toString(semitones) + "]";
    }

    private boolean allSet(boolean[] matches) {
      for (int i = 0; i < matches.length; i++) {
        if (!matches[i]) {
          return false;
        }
      }
      return true;
    }

    private boolean contains(int midiNote, boolean[] matches) {
      int commonNote = midiNote % Midi.SEMITONES_PER_OCTAVE;
      for (int i = 0; i < semitones.length; i++) {
        if (semitones[i] % Midi.SEMITONES_PER_OCTAVE == commonNote) {
          matches[i] = true;
          return true;
        }
      }
      return false;
    }
  }

  private static final int[] AUGMENTED = new int[] {
      0,
      4,
      8
  };

  private static final int[] DIMINISHED = new int[] {
      0,
      3,
      6
  };
  private static final int[] MAJOR = new int[] {
      0,
      4,
      7
  };

  private static final int[] MAJOR_SEVENTH = new int[] {
      0,
      4,
      7,
      11
  };

  private static final int[] MAJOR_NINTH = new int[] {
      0,
      4,
      7,
      11,
      14
  };

  private static final int[] MINOR = new int[] {
      0,
      3,
      7
  };

  private static final int[] MINOR_SEVENTH = new int[] {
      0,
      3,
      7,
      10
  };

  private static final int[] MINOR_NINTH = new int[] {
      0,
      3,
      7,
      10,
      14
  };

  private static final int[] SEVENTH = new int[] {
      0,
      4,
      7,
      10
  };

  private static final ChordIntervals[] CHORD_INTERVALS = new ChordIntervals[] {
      new ChordIntervals("Maj9", MAJOR_NINTH),
      new ChordIntervals("min9", MINOR_NINTH),
      new ChordIntervals("Maj7", MAJOR_SEVENTH),
      new ChordIntervals("min7", MINOR_SEVENTH),
      new ChordIntervals("7", SEVENTH),
      new ChordIntervals("Maj", MAJOR),
      new ChordIntervals("min", MINOR),
      new ChordIntervals("aug", AUGMENTED),
      new ChordIntervals("dim", DIMINISHED),
  };

  private static final List<ChordType> CHORD_TYPES = createChordTypes();

  public static List<ChordType> createChordTypes() {
    List<ChordType> chordTypes = new LinkedList<>();
    for (int root = 0; root < Midi.SEMITONES_PER_OCTAVE; root++) {
      for (ChordIntervals chordIntervals : CHORD_INTERVALS) {
        ChordType chordType = new ChordType(root, chordIntervals);
        chordTypes.add(chordType);
      }
    }
    return chordTypes;
  }

  public TreeSet<Chord> getChords(NavigableSet<Note> notes, int channel) {
    int matchedNotes = 0;
    TreeSet<Chord> channelChords = new TreeSet<>();
    RandomAccessList<Note> channelNotes = getChannelNotes(notes, channel);
    int noteIndex = 0;
    while (noteIndex < channelNotes.size()) {
      int matchLength = findChord(channelChords, channelNotes, noteIndex);
      if (matchLength == 0) {
        //System.out.println("Skipping note=" + channelNotes.get(noteIndex));
        noteIndex++;
      } else {
        matchedNotes += matchLength;
        noteIndex += matchLength;
      }
    }
    System.out.println("Matched " + matchedNotes + " out of " + channelNotes.size() + " notes");
    return channelChords;
  }

  private int findChord(TreeSet<Chord> channelChords, RandomAccessList<Note> channelNotes, int noteIndex) {
    int matchLength = 0;
    Iterator<ChordType> iterator = CHORD_TYPES.iterator();
    while (iterator.hasNext() && matchLength == 0) {
      ChordType chordType = iterator.next();
      matchLength = chordType.match(channelNotes, noteIndex);
      //System.out.println("noteIndex=" + noteIndex + ", chordType=" + chordType + ", matchLength=" + matchLength);
      if (matchLength > 0) {
        Note firstNote = channelNotes.get(noteIndex);
        Note lastNote = channelNotes.get(noteIndex + matchLength - 1);
        long tick = firstNote.getTick();
        long duration = (lastNote.getTick() + lastNote.getDuration()) - tick;
        Chord chord = new Chord(chordType, tick, duration);
        channelChords.add(chord);
      }
    }
    return matchLength;
  }

  private RandomAccessList<Note> getChannelNotes(NavigableSet<Note> notes, int channel) {
    RandomAccessList<Note> channelNotes = new DirectList<>();
    for (Note note : notes) {
      if (note.getChannel() == channel) {
        channelNotes.add(note);
      }
    }
    return channelNotes;
  }
}
