// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import com.example.afs.musicpad.song.Instruments;
import com.example.afs.musicpad.song.Midi;
import com.example.afs.musicpad.song.Song;

public class Analyzer {

  public static void displayKey(Song song) {
    int[] noteCount = song.getChannelNoteCount();
    int[][] commonNoteCount = song.getCommonNoteCount();
    System.out.println("CHN RNK KEY      SYNOPSIS ACCIDENTALS TRIADS THIRDS");
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (noteCount[channel] > 0) {
        if (channel != Midi.DRUM) {
          KeyScore[] keyScores = KeySignatures.getKeyScores(commonNoteCount[channel]);
          for (KeyScore keyScore : keyScores) {
            int rank = keyScore.getRank();
            if (rank == 1) {
              String key = keyScore.getKey();
              String synopsis = keyScore.getSynopsis();
              int accidentals = keyScore.getAccidentals();
              int triads = keyScore.getTriads();
              int thirds = keyScore.getThirds();
              System.out.printf("%3d %3d %-8s %-8s         %3d    %3d    %3d\n", channel, rank, key, synopsis, accidentals, triads, thirds);
            }
          }
        }
      }
    }
  }

  public static void displaySemitoneCounts(Song song) {
    // TODO: Song should not leak array implementation detail
    int[] noteCount = song.getChannelNoteCount();
    int[][] commonNoteCount = song.getCommonNoteCount();
    System.out.print("CHN   TOT OCC POL");
    for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
      System.out.printf(" %3s", Names.getNoteName(semitone));
    }
    System.out.println();
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (noteCount[channel] > 0) {
        if (channel != Midi.DRUM) {
          System.out.printf("%3d %5d %3d %3d", channel, noteCount[channel], song.getOccupancy(channel), song.getPolyphony(channel));
          for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
            int count = commonNoteCount[channel][semitone];
            System.out.printf(" %3d", count);
          }
          System.out.println(" " + song.getProgramNames(channel));
        }
      }
    }
    if (noteCount[Midi.DRUM] > 0) {
      System.out.println("CHN 9 TOT " + noteCount[Midi.DRUM]);
      int[][] distinctNoteCount = song.getDistinctNoteCount();
      for (int drum = 0; drum < Midi.NOTES; drum++) {
        int count = distinctNoteCount[Midi.DRUM][drum];
        if (count > 0) {
          System.out.printf("%3d [%s]\n", count, Instruments.getDrumName(drum));
        }
      }
    }
  }

}
