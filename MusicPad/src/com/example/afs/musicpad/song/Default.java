// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

import com.example.afs.musicpad.midi.Midi;

public class Default {

  public static final int BEATS_PER_MEASURE = 4;
  public static final int TICKS_PER_BEAT = 1024;
  public static final int BEATS_PER_MINUTE = 120;
  public static final int BEAT_UNIT = 4;
  public static final int GAP_BEAT_UNIT = 64;
  public static final int OCTAVE = 4;
  public static final int OCTAVE_SEMITONE = OCTAVE * Midi.SEMITONES_PER_OCTAVE;
  public static final int RESOLUTION = TICKS_PER_BEAT / 2;

}
