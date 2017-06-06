// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import java.util.Arrays;

public class KeySignatures {

  // See Miller p. 30 regarding enharmonic scales:
  // C# Major and Db Major
  // F# Major and Gb Major
  // B Major and Cb Major
  // The last six key signatures in this table are enharmonic
  private static final KeySignature[] keySignatures = new KeySignature[] {
      new KeySignature("101011010101", 0, 9, true, 0), // C Maj/A min
      new KeySignature("101011010101", 9, 0, false, 0), // A min/C Maj
      new KeySignature("101010110101", 7, 4, true, 1), // G Maj/E min (F#)
      new KeySignature("101010110101", 4, 7, false, 1), // E min/G Maj (F#)
      new KeySignature("101011010110", 5, 2, true, -1), // F Maj/D min (Bb)
      new KeySignature("101011010110", 2, 5, false, -1), // D min/F Maj (Bb)
      new KeySignature("011010110101", 2, 11, true, 2), // D Maj/B min (C#, F#)
      new KeySignature("011010110101", 11, 2, false, 2), // B min/D Maj (C#, F#)
      new KeySignature("101101010110", 10, 7, true, -2), // Bb Maj/G min (Bb, Eb)
      new KeySignature("101101010110", 7, 10, false, -2), // G min/Bb Maj (Bb, Eb)
      new KeySignature("011010101101", 9, 6, true, 3), // A Maj/F# min (C#, F#, G#)
      new KeySignature("011010101101", 6, 9, false, 3), // F# min/A Maj (C#, F#, G#)
      new KeySignature("101101011010", 3, 0, true, -3), // Eb Maj/C min (Bb, Eb, Ab)
      new KeySignature("101101011010", 0, 3, false, -3), // C min/Eb Maj (Bb, Eb, Ab)
      new KeySignature("010110101101", 4, 1, true, 4), // E Maj/C# min (C#, D#, F#, G#)
      new KeySignature("010110101101", 1, 4, false, 4), // C# min/E Maj (C#, D#, F#, G#)
      new KeySignature("110101011010", 8, 5, true, -4), // Ab Maj/F min (Bb, Db, Eb, Ab)
      new KeySignature("110101011010", 5, 8, false, -4), // F min/Ab Maj (Bb, Db, Eb, Ab)
      new KeySignature("010110101011", 11, 8, true, 5), // B Maj/G# min (C#, D#, F#, G#, A#)
      new KeySignature("010110101011", 8, 11, false, 5), // G# min/B Maj (C#, D#, F#, G#, A#)
      new KeySignature("110101101010", 1, 10, true, -5), // Db Maj/Bb min (Bb, Db, Eb, Gb, Ab)
      new KeySignature("110101101010", 10, 1, false, -5), // Bb min/Db Maj (Bb, Db, Eb, Gb, Ab)
      new KeySignature("010101101011", 6, 3, true, 6), // F# Maj/D# min (C#, D#, E#, F#, G#, A#)
      new KeySignature("010101101011", 3, 6, false, 6), // D# min/F# Maj (C#, D#, E#, F#, G#, A#)
  //new KeySignature("010101101011", 6, 3, true, -6), // Gb Maj/Eb min (Bb, Cb, Db, Eb, Gb, Ab)
  //new KeySignature("010101101011", 3, 6, false, -6), // Eb min/Gb Maj (Bb, Cb, Db, Eb, Gb, Ab)
  //new KeySignature("110101101010", 1, 10, true, 7), // C# Maj/A# min (C#, D#, E#, F#, G#, A#, B#)
  //new KeySignature("110101101010", 10, 1, false, 7), // A# min/C# Maj (C#, D#, E#, F#, G#, A#, B#)
  //new KeySignature("010110101011", 11, 8, true, -7), // Cb Maj/Ab min (Bb, Cb, Db, Eb, Fb, Gb, Ab)
  //new KeySignature("010110101011", 8, 11, false, -7), // Ab min/Cb Maj (Bb, Cb, Db, Eb, Fb, Gb, Ab)
  };

  public static KeyScore[] getKeyScores(int[] chromaticCounts) {
    KeyScore[] keyScores = new KeyScore[keySignatures.length];
    for (int i = 0; i < keySignatures.length; i++) {
      KeySignature keySignature = keySignatures[i];
      keyScores[i] = keySignature.getKeyScore(chromaticCounts);
    }
    Arrays.sort(keyScores);
    for (int i = 0; i < keySignatures.length; i++) {
      if ((i > 0 && keyScores[i].isTieScore(keyScores[i - 1]))) {
        keyScores[i].setRank(keyScores[i - 1].getRank());
      } else {
        keyScores[i].setRank(i + 1);
      }
    }
    return keyScores;
  }

}
