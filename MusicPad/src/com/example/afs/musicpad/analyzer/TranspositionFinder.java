// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;

public class TranspositionFinder {

  public static final boolean[] isWhite = new boolean[] {
      true, // C
      false, // C#
      true, // D
      false, // D#
      true, // E
      true, // F
      false, // F#
      true, // G
      false, // G#
      true, // A
      false, // A#
      true, //B
  };

  public static int getDistanceToWhiteKeys(Song song) {
    int bestTransposition = 0;
    int bestScore = Integer.MIN_VALUE;
    for (int transposeDistance = -6; transposeDistance < 6; transposeDistance++) {
      int naturals = 0;
      int accidentals = 0;
      for (int channel = 0; channel < Midi.CHANNELS; channel++) {
        if (channel != Midi.DRUM) {
          for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
            int semitoneCount = song.getCommonNoteCounts(channel)[semitone];
            if (semitoneCount > 0) {
              int transposedSemitone = normalize(semitone + transposeDistance);
              if (isWhite[transposedSemitone]) {
                naturals += semitoneCount;
              } else {
                accidentals += semitoneCount;
              }
            }
          }
        }
      }
      int score = naturals - accidentals;
      if (score > bestScore || (score == bestScore && transposeDistance == 0)) {
        bestScore = score;
        bestTransposition = transposeDistance;
      }
    }
    return bestTransposition;
  }

  private static int normalize(int deltaSemitone) {
    return (Midi.SEMITONES_PER_OCTAVE + deltaSemitone) % Midi.SEMITONES_PER_OCTAVE;
  }
}
