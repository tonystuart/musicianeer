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

import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.theory.ChordType;
import com.example.afs.musicpad.theory.Intervals;
import com.example.afs.musicpad.theory.Keys;

public class ChordPlayer extends Player {

  private int baseMidiNote;
  private Map<ChordType, Integer> chordTypeToMidiNoteIndex = new HashMap<>();
  private Map<Integer, ChordType> midiNoteIndexToChordType = new HashMap<>();

  public ChordPlayer(DeviceHandler deviceHandler, Song song) {
    super(deviceHandler, song);
    initializeOctave();
  }

  @Override
  public void play(Action action, int midiNote) {
    int midiNoteIndex = midiNote - baseMidiNote;
    ChordType chordType = midiNoteIndexToChordType.get(midiNoteIndex);
    if (chordType != null) {
      playMidiChord(action, baseMidiNote, chordType);
    }
  }

  @Override
  public String toKeyCap(ChordType chordType) {
    Integer midiNoteIndex = chordTypeToMidiNoteIndex.get(chordType);
    if (midiNoteIndex == null) {
      midiNoteIndex = Keys.CMajorFull.getMidiNotes()[chordTypeToMidiNoteIndex.size()];
      chordTypeToMidiNoteIndex.put(chordType, midiNoteIndex);
      midiNoteIndexToChordType.put(midiNoteIndex, chordType);
      System.out.println(chordType + " <=> " + midiNoteIndex);
    }
    String keyCap = inputMapping.toKeyCap(baseMidiNote + midiNoteIndex);
    return keyCap;
  }

  @Override
  public String toKeyCap(int midiNote) {
    ChordType chordType = new ChordType(midiNote % Midi.SEMITONES_PER_OCTAVE, new Intervals(Names.getNoteName(midiNote), 0));
    String keyCap = toKeyCap(chordType);
    return keyCap;
  }

  private void initializeOctave() {
    int averageMidiNote = song.getAverageMidiNote(songChannel);
    int octave = averageMidiNote / Midi.SEMITONES_PER_OCTAVE;
    baseMidiNote = octave * Midi.SEMITONES_PER_OCTAVE;
    inputMapping.setOctave(octave);
  }

}
