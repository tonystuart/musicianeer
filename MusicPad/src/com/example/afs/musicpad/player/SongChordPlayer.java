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
import com.example.afs.musicpad.CommandProcessor;
import com.example.afs.musicpad.analyzer.ChordFinder;
import com.example.afs.musicpad.device.CharCode;
import com.example.afs.musicpad.song.Chord;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.theory.ChordType;

public class SongChordPlayer extends SongPlayer {

  private TreeSet<Chord> chords;
  private ChordType[] buttonIndexToChord;
  private Map<ChordType, String> chordToKeySequence;

  public SongChordPlayer(Synthesizer synthesizer, Song song, int channel) {
    super(synthesizer, song, channel);
    ChordFinder chordFinder = new ChordFinder();
    chords = chordFinder.getChords(song.getNotes(), channel);
    buttonIndexToChord = getUniqueChordTypes(chords);
    chordToKeySequence = new HashMap<>();
    System.out.println("Total chords: " + chords.size() + ", Unique chords: " + buttonIndexToChord.length);
    for (int buttonIndex = 0; buttonIndex < buttonIndexToChord.length; buttonIndex++) {
      ChordType chordType = buttonIndexToChord[buttonIndex];
      String keySequence = CharCode.fromIndexToSequence(buttonIndex);
      chordToKeySequence.put(chordType, keySequence);
      System.out.println(keySequence + " -> " + chordType);
    }
    setTitle("Channel " + (channel + 1) + " Chords");
  }

  @Override
  public int getUniqueCount() {
    return buttonIndexToChord.length;
  }

  @Override
  public void play(Action action, int chordIndex) {
    if (chordIndex < buttonIndexToChord.length) {
      ChordType chordType = buttonIndexToChord[chordIndex];
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
        if (CommandProcessor.isTraceMusic()) {
          System.out.println("SongChordPlayer.getMusic: tick=" + chord.getTick() + ", duration=" + chord.getDuration() + ", chordType=" + chordType.getName());
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
