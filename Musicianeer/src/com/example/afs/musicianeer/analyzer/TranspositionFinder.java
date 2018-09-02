// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.analyzer;

import com.example.afs.musicianeer.main.Musicianeer;
import com.example.afs.musicianeer.midi.Midi;
import com.example.afs.musicianeer.song.Song;

public class TranspositionFinder {

  public static class EasyTransposition {
    private int songTransposition;
    private int[] channelTranspositions;

    public int[] getChannelTranspositions() {
      return channelTranspositions;
    }

    public int getSongTransposition() {
      return songTransposition;
    }
  }

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

  public EasyTransposition findEasyTransposition(Song song) {
    EasyTransposition easyTransposition = new EasyTransposition();
    easyTransposition.songTransposition = getSongTransposition(song);
    easyTransposition.channelTranspositions = getChannelTranspositions(song, easyTransposition.songTransposition, Musicianeer.LOWEST_NOTE, Musicianeer.HIGHEST_NOTE);
    return easyTransposition;
  }

  private int getBestTransposition(Song song) {
    int bestTransposition = 0;
    int bestScore = Integer.MIN_VALUE;
    for (int transposeDistance = -6; transposeDistance < 6; transposeDistance++) {
      int naturals = 0;
      int accidentals = 0;
      for (int channel = 0; channel < Midi.CHANNELS; channel++) {
        if (channel != Midi.DRUM) {
          for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
            int chromaticCount = song.getChromaticNoteCounts(channel)[semitone];
            if (chromaticCount > 0) {
              int transposedSemitone = normalize(semitone + transposeDistance);
              if (isWhite[transposedSemitone]) {
                naturals += chromaticCount;
              } else {
                accidentals += chromaticCount;
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

  private int[] getChannelTranspositions(Song song, int transposition, int lowestPlayableNote, int highestPlayableNote) {
    int[] channelTranspositions = new int[Midi.CHANNELS];
    for (int channel : song.getActiveChannels()) {
      int lowestMidiNote = song.getLowestMidiNote(channel);
      int highestMidiNote = song.getHighestMidiNote(channel);
      if (channel != Midi.DRUM) {
        lowestMidiNote += transposition;
        highestMidiNote += transposition;
      }
      if (lowestMidiNote < lowestPlayableNote) {
        channelTranspositions[channel] = (((lowestPlayableNote - lowestMidiNote) / Midi.SEMITONES_PER_OCTAVE) + 1) * Midi.SEMITONES_PER_OCTAVE; // positive
      } else if (highestMidiNote > highestPlayableNote) {
        channelTranspositions[channel] = (((highestPlayableNote - highestMidiNote) / Midi.SEMITONES_PER_OCTAVE) - 1) * Midi.SEMITONES_PER_OCTAVE; // negative
      }
    }
    return channelTranspositions;
  }

  private int getSongTransposition(Song song) {
    int bestTransposition = getBestTransposition(song);
    int songTransposition = 0;
    if (bestTransposition < 0) {
      int minimumTransposition = song.getMinimumTransposition();
      if (Math.abs(bestTransposition) < Math.abs(minimumTransposition)) {
        songTransposition = bestTransposition;
      }
    } else if (bestTransposition > 0) {
      int maximumTransposition = song.getMaximumTransposition();
      if (bestTransposition < maximumTransposition) {
        songTransposition = bestTransposition;
      }
    }
    return songTransposition;
  }

  private int normalize(int deltaSemitone) {
    return (Midi.SEMITONES_PER_OCTAVE + deltaSemitone) % Midi.SEMITONES_PER_OCTAVE;
  }
}
