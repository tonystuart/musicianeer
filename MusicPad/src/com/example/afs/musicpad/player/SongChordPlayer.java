// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.analyzer.ChordFinder;
import com.example.afs.musicpad.device.common.InputMapping;
import com.example.afs.musicpad.song.Chord;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.theory.ChordType;

public class SongChordPlayer extends SongPlayer {

  private TreeSet<Chord> chords;
  private ChordType[] noteIndexToChord;
  private Map<ChordType, String> chordToKeySequence;

  public SongChordPlayer(Synthesizer synthesizer, Song song, int channel, InputMapping inputMapping) {
    super(synthesizer, song, channel);
    ChordFinder chordFinder = new ChordFinder();
    chords = chordFinder.getChords(song.getNotes(), channel);
    noteIndexToChord = getUniqueChordTypes(chords);
    chordToKeySequence = new HashMap<>();
    System.out.println("Total chords: " + chords.size() + ", Unique chords: " + noteIndexToChord.length);
    updateInputDevice(inputMapping);
    setTitle("Channel " + (channel + 1) + " Chords");
  }

  @Override
  public void play(Action action, int chordIndex) {
    if (chordIndex < noteIndexToChord.length) {
      ChordType chordType = noteIndexToChord[chordIndex];
      playMidiChord(action, Default.OCTAVE_SEMITONE, chordType);
    }
  }

  @Override
  public void updateInputDevice(InputMapping inputMapping) {
    for (int noteIndex = 0; noteIndex < noteIndexToChord.length; noteIndex++) {
      ChordType chordType = noteIndexToChord[noteIndex];
      String keySequence = inputMapping.fromIndexToSequence(noteIndex);
      chordToKeySequence.put(chordType, keySequence);
      System.out.println(keySequence + " -> " + chordType);
    }
  }

  @Override
  protected String getMusic(long currentTick, long firstTick, long lastTick, int ticksPerCharacter) {
    StringBuilder s = new StringBuilder();
    long untilTick;
    Chord previousChord = chords.lower(new Chord(currentTick));
    if (previousChord == null) {
      untilTick = 0;
    } else {
      untilTick = previousChord.getTick() + previousChord.getDuration();
    }
    for (long tick = firstTick; tick < lastTick; tick += ticksPerCharacter) {
      long nextTick = tick + ticksPerCharacter;
      if (currentTick >= tick && currentTick < nextTick) {
        s.append(">");
      } else {
        s.append(" ");
      }
      SortedSet<Chord> tickChords = chords.subSet(new Chord(tick), new Chord(nextTick));
      int chordCount = tickChords.size();
      if (chordCount == 0) {
        if (tick < untilTick) {
          s.append("~");
        } else {
          s.append(".");
        }
      } else {
        if (chordCount > 1) {
          System.out.println("Squeezing " + chordCount + " chords into space for one chord");
        }
        for (Chord chord : tickChords) {
          ChordType chordType = chord.getChordType();
          String keySequence = chordToKeySequence.get(chordType);
          if (keySequence.length() > 1) {
            //System.out.println("Squeezing " + keySequence.length() + " characters into space for one character");
          }
          s.append(keySequence);
          untilTick = chord.getTick() + chord.getDuration();
        }
      }
    }
    return s.toString();
  }

  private ChordType[] getUniqueChordTypes(TreeSet<Chord> chords) {
    Set<ChordType> treeSet = new TreeSet<>();
    for (Chord chord : chords) {
      ChordType chordType = chord.getChordType();
      treeSet.add(chordType);
    }
    int chordIndex = 0;
    int uniqueChordCount = treeSet.size();
    ChordType[] uniqueChordTypes = new ChordType[uniqueChordCount];
    for (ChordType uniqueChord : treeSet) {
      uniqueChordTypes[chordIndex] = uniqueChord;
      chordIndex++;
    }
    return uniqueChordTypes;
  }

}
