// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.analyzer.ChordFinder;
import com.example.afs.musicpad.analyzer.ChordFinder.Chord;
import com.example.afs.musicpad.analyzer.ChordFinder.ChordType;
import com.example.afs.musicpad.song.Song;

public class ChordPlayer extends Player {

  private static final int OCTAVE_BASE = 48;

  private ChordType[] numberToChord;
  private Map<ChordType, String> chordToNumber;

  public ChordPlayer(Synthesizer synthesizer, Song song, int channel) {
    super(synthesizer, song, channel);
    ChordFinder chordFinder = new ChordFinder();
    TreeSet<Chord> chords = chordFinder.getChords(song.getNotes(), channel);
    Set<ChordType> uniqueChords = new HashSet<>();
    for (Chord chord : chords) {
      ChordType chordType = chord.getChordType();
      uniqueChords.add(chordType);
    }
    int chordIndex = 0;
    int uniqueChordCount = uniqueChords.size();
    numberToChord = new ChordType[uniqueChordCount];
    for (ChordType uniqueChord : uniqueChords) {
      numberToChord[chordIndex] = uniqueChord;
      chordIndex++;
    }
    Arrays.sort(numberToChord, (o1, o2) -> compare(o1, o2));
    chordToNumber = new HashMap<>();
    for (int i = 0; i < numberToChord.length; i++) {
      chordToNumber.put(numberToChord[i], Integer.toString(i));
    }
    System.out.println("Total chords: " + chords.size() + ", Unique chords: " + uniqueChords.size());
    for (int i = 0; i < numberToChord.length; i++) {
      System.out.println(i + " -> " + numberToChord[i].getName());
    }
  }

  @Override
  public int getUniqueCount() {
    return numberToChord.length;
  }

  @Override
  public void play(Action action, int digit) {
    int chordIndex = page * ITEMS_PER_PAGE + digit;
    if (chordIndex < numberToChord.length) {
      ChordType chordType = numberToChord[chordIndex];
      for (int semitone : chordType.getSemitones()) {
        try {
          playMidiNote(action, OCTAVE_BASE + semitone);
          Thread.sleep(0);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private int compare(ChordType left, ChordType right) {
    int[] leftSemitones = left.getSemitones();
    int[] rightSemitones = right.getSemitones();
    int limit = Math.min(leftSemitones.length, rightSemitones.length);
    for (int i = 0; i < limit; i++) {
      int deltaSemitone = leftSemitones[i] - rightSemitones[i];
      if (deltaSemitone != 0) {
        return deltaSemitone;
      }
    }
    int deltaLength = leftSemitones.length - rightSemitones.length;
    if (deltaLength != 0) {
      return deltaLength;
    }
    return 0;
  }

}
