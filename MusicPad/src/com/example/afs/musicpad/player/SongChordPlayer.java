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
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.analyzer.ChordFinder;
import com.example.afs.musicpad.device.CharCode;
import com.example.afs.musicpad.song.Chord;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.theory.ChordType;

public class SongChordPlayer extends SongPlayer {

  private TreeSet<Chord> chords;
  private ChordType[] playIndexToChord;
  private Map<ChordType, String> chordToKeySequence;

  public SongChordPlayer(Synthesizer synthesizer, Song song, int channel) {
    super(synthesizer, song, channel);
    ChordFinder chordFinder = new ChordFinder();
    chords = chordFinder.getChords(song.getNotes(), channel);
    playIndexToChord = getUniqueChordTypes(chords);
    chordToKeySequence = new HashMap<>();
    System.out.println("Total chords: " + chords.size() + ", Unique chords: " + playIndexToChord.length);
    for (int playIndex = 0; playIndex < playIndexToChord.length; playIndex++) {
      ChordType chordType = playIndexToChord[playIndex];
      String keySequence = CharCode.fromIndexToSequence(playIndex);
      chordToKeySequence.put(chordType, keySequence);
      System.out.println(keySequence + " -> " + chordType);
    }
    setTitle("Channel " + (channel + 1) + " Chords");
  }

  @Override
  public int getUniqueCount() {
    return playIndexToChord.length;
  }

  @Override
  public void play(Action action, int chordIndex) {
    if (chordIndex < playIndexToChord.length) {
      ChordType chordType = playIndexToChord[chordIndex];
      playMidiChord(action, Default.OCTAVE_SEMITONE, chordType);
    }
  }

  @Override
  protected String getMusic(long firstTick, long lastTick) {
    StringBuilder s = new StringBuilder();
    NavigableSet<Chord> tickChords = chords.subSet(new Chord(firstTick), false, new Chord(lastTick), true);
    if (tickChords.size() > 0) {
      Chord first = tickChords.first();
      long firstChordTick = first.getTick();
      s.append(getIntroTicks(firstTick, firstChordTick));
      for (Chord chord : tickChords) {
        ChordType chordType = chord.getChordType();
        String keySequence = chordToKeySequence.get(chordType);
        //s.append(chordType.getName() + " (" + keySequence + ") ");
        s.append(keySequence + " ");
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
