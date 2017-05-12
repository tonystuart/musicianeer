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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.example.afs.musicpad.analyzer.ChordFinder;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Chord;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.theory.ChordType;
import com.example.afs.musicpad.theory.Keys;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class ChordPlayer extends Player {

  private int baseMidiNote;
  private TreeSet<Chord> chords;
  private Map<ChordType, Integer> chordTypeToMidiNoteIndex = new HashMap<>();
  private Map<Integer, ChordType> midiNoteIndexToChordType = new HashMap<>();

  public ChordPlayer(DeviceHandler deviceHandler, Song song) {
    super(deviceHandler, song);
    initializeOctave();
    initializeChords();
  }

  @Override
  public void play(Action action, int midiNote) {
    int midiNoteIndex = midiNote - baseMidiNote;
    ChordType chordType = midiNoteIndexToChordType.get(midiNoteIndex);
    if (chordType != null) {
      playMidiChord(action, baseMidiNote, chordType);
    }
  }

  private void initializeChords() {
    ChordFinder chordFinder = new ChordFinder();
    chords = chordFinder.getChords(song.getNotes(), songChannel);
    Set<ChordType> uniqueChordTypes = new HashSet<>();
    for (Chord chord : chords) {
      uniqueChordTypes.add(chord.getChordType());
    }
    RandomAccessList<ChordType> chordMapping = new DirectList<>(uniqueChordTypes);
    chordMapping.sort((o1, o2) -> o1.compareTo(o2));
    for (int i = 0; i < chordMapping.size(); i++) {
      int midiNoteIndex = Keys.CMajorFull.getMidiNotes()[i];
      chordTypeToMidiNoteIndex.put(chordMapping.get(i), midiNoteIndex);
      midiNoteIndexToChordType.put(midiNoteIndex, chordMapping.get(i));
      System.out.println(chordMapping.get(i) + " <=> " + midiNoteIndex);
    }
  }

  private void initializeOctave() {
    int averageMidiNote = song.getAverageMidiNote(songChannel);
    int octave = averageMidiNote / Midi.SEMITONES_PER_OCTAVE;
    baseMidiNote = octave * Midi.SEMITONES_PER_OCTAVE;
    inputMapping.setOctave(octave);
  }

}
