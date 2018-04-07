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

public class Keyboard {

  private static final boolean[] IS_NATURAL = {
      true, // 60, C
      false, // 61, C#
      true, // 62, D
      false, // 63, D#
      true, // 64, E
      true, // 65, F
      false, // 66, F#
      true, // 67, G
      false, // 68, G#
      true, // 69, A
      false, // 70, A#
      true, // 71, B
  };

  public static boolean isNatural(int midiNote) {
    return IS_NATURAL[midiNote % Midi.SEMITONES_PER_OCTAVE];
  }

  public static int roundToNatural(int midiNote) {
    int semitone = midiNote % Midi.SEMITONES_PER_OCTAVE;
    int white = IS_NATURAL[semitone] ? midiNote : midiNote - 1;
    return white;
  }

}
