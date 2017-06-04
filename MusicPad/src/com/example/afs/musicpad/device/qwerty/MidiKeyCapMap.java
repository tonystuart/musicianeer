// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import com.example.afs.musicpad.player.Chord;
import com.example.afs.musicpad.renderer.Notator.KeyCap;
import com.example.afs.musicpad.renderer.Notator.Slice;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class MidiKeyCapMap implements KeyCapMap {

  public MidiKeyCapMap(RandomAccessList<Slice> slices) {
  }

  @Override
  public RandomAccessList<KeyCap> getKeyCaps() {
    return new DirectList<>();
  }

  @Override
  public Chord onDown(int inputCode) {
    // TODO: Use a common base class with a derived class that handles single notes
    RandomAccessList<Note> notes = new DirectList<>();
    notes.add(new Note.NoteBuilder().withMidiNote(inputCode).create());
    return new Chord(notes);
  }

  @Override
  public void onUp(int inputCode) {
  }

}
