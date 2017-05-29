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

import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.theory.Keys;

public class ChordPlayer extends Player {

  private Map<Chord, Integer> chordToMidiNote = new HashMap<>();
  private Map<Integer, Chord> midiNoteToChord = new HashMap<>();
  private int mappingBase;

  public ChordPlayer(DeviceHandler deviceHandler, Song song) {
    super(deviceHandler, song);
  }

  @Override
  public void play(Action action, int midiNote) {
    Chord chord = midiNoteToChord.get(midiNote - mappingBase);
    if (chord != null) {
      playMidiChord(action, chord);
    }
  }

  @Override
  public String toKeyCap(Chord chord) {
    Integer midiNote = chordToMidiNote.get(chord);
    if (midiNote == null) {
      midiNote = Keys.CMajorFull.getNoteInKey(chordToMidiNote.size());
      chordToMidiNote.put(chord, midiNote);
      midiNoteToChord.put(midiNote, chord);
      System.out.println(chord + " <=> " + midiNote);
    }
    String keyCap = numericMapping.toKeyCap(mappingBase + midiNote);
    return keyCap;
  }

  @Override
  public String toKeyCap(int midiNote) {
    Chord chord = new Chord(midiNote);
    String keyCap = toKeyCap(chord);
    return keyCap;
  }

}
