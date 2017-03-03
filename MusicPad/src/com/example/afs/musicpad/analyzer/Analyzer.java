// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;

public class Analyzer {

  public static void displayDrumCounts(Song song) {
    int drumBeatCount = song.getChannelNoteCount(Midi.DRUM);
    if (drumBeatCount > 0) {
      System.out.println("CHN 9 TOT " + drumBeatCount);
      int[] distinctNoteCount = song.getDistinctNoteCount(Midi.DRUM);
      for (int drum = 0; drum < Midi.NOTES; drum++) {
        int count = distinctNoteCount[drum];
        if (count > 0) {
          System.out.printf("%4d [%s]\n", count, Instruments.getDrumName(drum));
        }
      }
    }
  }

  public static void displayKey(Song song) {
    System.out.println("CHN RNK KEY      SYNOPSIS ACCIDENTALS TRIADS THIRDS");
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (song.getChannelNoteCount(channel) > 0) {
        if (channel != Midi.DRUM) {
          int[] commonNoteCounts = song.getCommonNoteCounts(channel);
          KeyScore[] keyScores = KeySignatures.getKeyScores(commonNoteCounts);
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
    System.out.print("CHN   TOT OCC CON");
    for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
      System.out.printf(" %3s", Names.getNoteName(semitone));
    }
    System.out.println();
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (song.getChannelNoteCount(channel) > 0) {
        if (channel != Midi.DRUM) {
          int channelNoteCount = song.getChannelNoteCount(channel);
          int occupancy = song.getOccupancy(channel);
          int concurrency = song.getConcurrency(channel);
          System.out.printf("%3d %5d %3d %3d", channel, channelNoteCount, occupancy, concurrency);
          for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
            int commonNoteCount = song.getCommonNoteCounts(channel)[semitone];
            System.out.printf(" %3d", commonNoteCount);
          }
          System.out.println(" " + song.getProgramNames(channel));
        }
      }
    }
  }
}
