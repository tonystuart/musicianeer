// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.midi;

// See https://www.midi.org/specifications/item/gm-level-1-sound-set

public class Midi {

  public static final int CHANNELS = 16;
  public static final int NOTES = 128;
  public static final int PROGRAMS = 128;
  public static final int VELOCITIES = 128;

  public static final int MIN_VALUE = 0;
  public static final int MAX_VALUE = 127;

  public static final int PIANO = 0;
  public static final int DRUM = 9;

  public static final int DRUM_BASE = 33;

  public static final int MM_TEMPO = 0x51;
  public static final int MM_TIME_SIGNATURE = 0x58;
  public static final int MM_LYRIC = 5;
  public static final int MM_TEXT = 1;

  public static final int NOTES_PER_OCTAVE = 7; // notes per octave
  public static final int SEMITONES_PER_OCTAVE = 12; // semitones per octave

  public static final int SEMITONES_TO_MINOR_THIRD = 3;
  public static final int SEMITONES_TO_MAJOR_THIRD = 4;
  public static final int SEMITONES_TO_PERFECT_FIFTH = 7;

}
