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
  public static final char SHIFT = 16;
  public static final char SEMICOLON = 186;
  public static final char SINGLEQUOTE = 222;

  public static final int UNDEFINED = 0;

  private static final byte[] keyCodeToMidiNote = new byte[256];
  private static final char[] midiNoteToLegend = new char[Midi.MAX_VALUE];
  private static final char[] midiNoteToKeyCode = new char[Midi.MAX_VALUE];

  static {
    add(MidiNotes.C2, 'q', 'Q');
    add(MidiNotes.D2, 'w', 'W');
    add(MidiNotes.E2, 'e', 'E');
    add(MidiNotes.F2, 'r', 'R');
    add(MidiNotes.G2, 't', 'T');
    add(MidiNotes.A3, 'y', 'Y');
    add(MidiNotes.B3, 'u', 'U');
    add(MidiNotes.C3, 'i', 'I');
    add(MidiNotes.D3, 'o', 'O');
    add(MidiNotes.E3, 'p', 'P');
    add(MidiNotes.F3, 'a', 'A');
    add(MidiNotes.G3, 's', 'S');
    add(MidiNotes.A4, 'd', 'D');
    add(MidiNotes.B4, 'f', 'F');
    add(MidiNotes.C4, 'g', 'G');
    add(MidiNotes.D4, 'h', 'H');
    add(MidiNotes.E4, 'j', 'J');
    add(MidiNotes.F4, 'k', 'K');
    add(MidiNotes.G4, 'l', 'L');
    add(MidiNotes.A5, ';', SEMICOLON);
    add(MidiNotes.B5, '\'', SINGLEQUOTE);
    add(MidiNotes.C5, 'z', 'Z');
    add(MidiNotes.D5, 'x', 'X');
    add(MidiNotes.E5, 'c', 'C');
    add(MidiNotes.F5, 'v', 'V');
    add(MidiNotes.G5, 'b', 'B');
    add(MidiNotes.A6, 'n', 'N');
    add(MidiNotes.B6, 'm', 'M');
    add(MidiNotes.C6, ',', 'M');
  }

  public static int toKeyCode(int midiNote) {
    return midiNoteToKeyCode[midiNote];
  }

  public static char toLegend(int midiNote) {
    return midiNoteToLegend[midiNote];
  }

  public static int toMidiNote(int keyCode) {
    return keyCodeToMidiNote[keyCode];
  }

  private static void add(byte midiNote, char legend, char keyCode) {
    midiNoteToKeyCode[midiNote] = keyCode;
    midiNoteToLegend[midiNote] = legend;
    keyCodeToMidiNote[keyCode] = midiNote;
  }
}
