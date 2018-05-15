// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.midi.Midi;

public class KeyMap {

  // See: https://lists.w3.org/Archives/Public/www-dom/2010JulSep/att-0182/keyCode-spec.html
  public static final int SHIFT = 16;
  public static final int SEMICOLON = 186;
  public static final int SINGLEQUOTE = 222;

  public static final int UNDEFINED = 0;

  private static final int[] keyCodeToMidiNote = new int[256];
  private static final int[] midiNoteToKeyCode = new int[Midi.MAX_VALUE];

  static {
    add(MidiNotes.C2, 'Q');
    add(MidiNotes.D2, 'W');
    add(MidiNotes.E2, 'E');
    add(MidiNotes.F2, 'R');
    add(MidiNotes.G2, 'T');
    add(MidiNotes.A3, 'Y');
    add(MidiNotes.B3, 'U');
    add(MidiNotes.C3, 'I');
    add(MidiNotes.D3, 'O');
    add(MidiNotes.E3, 'P');
    add(MidiNotes.F3, 'A');
    add(MidiNotes.G3, 'S');
    add(MidiNotes.A4, 'D');
    add(MidiNotes.B4, 'F');
    add(MidiNotes.C4, 'G');
    add(MidiNotes.D4, 'H');
    add(MidiNotes.E4, 'J');
    add(MidiNotes.F4, 'K');
    add(MidiNotes.G4, 'L');
    add(MidiNotes.A5, SEMICOLON);
    add(MidiNotes.B5, SINGLEQUOTE);
    add(MidiNotes.C5, 'Z');
    add(MidiNotes.D5, 'X');
    add(MidiNotes.E5, 'C');
    add(MidiNotes.F5, 'V');
    add(MidiNotes.G5, 'B');
    add(MidiNotes.A6, 'N');
    add(MidiNotes.B6, 'M');
  }

  public static int toKeyCode(int midiNote) {
    return midiNoteToKeyCode[midiNote];
  }

  public static int toMidiNote(int keyCode) {
    return keyCodeToMidiNote[keyCode];
  }

  private static void add(int midiNote, int keyCode) {
    midiNoteToKeyCode[midiNote] = keyCode;
    keyCodeToMidiNote[keyCode] = midiNote;
  }
}
