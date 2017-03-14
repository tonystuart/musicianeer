// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Chord;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.theory.ChordType;
import com.example.afs.musicpad.theory.Chords;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class ChordFinder {

  public static class ChordMatcher {
    public int match(int[] semitones, RandomAccessList<Note> notes, int noteIndex) {
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
        if ((maxTick + gap) < tick || tick > (nextMeasureTick - gap) || !contains(semitones, note.getMidiNote(), matches)) {
          isMatch = false;
        } else {
          isMatch = true;
          maxTick = Math.max(maxTick, tick + duration);
          chordIndex++;
        }
      }
      return allSet(matches) ? chordIndex : 0;
    }

    private boolean allSet(boolean[] matches) {
      for (int i = 0; i < matches.length; i++) {
        if (!matches[i]) {
          return false;
        }
      }
      return true;
    }

    private boolean contains(int[] semitones, int midiNote, boolean[] matches) {
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
    ChordMatcher chordMatcher = new ChordMatcher();
    List<ChordType> chords = Chords.getChords();
    Iterator<ChordType> iterator = chords.iterator();
    while (iterator.hasNext() && matchLength == 0) {
      ChordType chordType = iterator.next();
      matchLength = chordMatcher.match(chordType.getMidiNotes(), channelNotes, noteIndex);
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
