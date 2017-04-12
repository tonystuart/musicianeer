// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import java.util.TreeSet;

import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Note.NoteBuilder;
import com.example.afs.musicpad.song.Song;

public class Transposer {

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

  public int getDistanceToWhiteKeys(Song song) {
    int bestTransposition = 0;
    int bestScore = Integer.MIN_VALUE;
    for (int transposeDistance = -11; transposeDistance <= 0; transposeDistance++) {
      int naturals = 0;
      int accidentals = 0;
      for (int channel = 0; channel < Midi.CHANNELS; channel++) {
        if (channel != Midi.DRUM) {
          for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
            int semitoneCount = song.getCommonNoteCounts(channel)[semitone];
            if (semitoneCount > 0) {
              int transposedSemitone = semitone + transposeDistance;
              int x = (Midi.SEMITONES_PER_OCTAVE + transposedSemitone) % Midi.SEMITONES_PER_OCTAVE;
              if (isWhite[x]) {
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

  public void transpose(Song oldSong, Song newSong, int distance) {
    TreeSet<Note> oldNotes = oldSong.getNotes();
    for (Note oldNote : oldNotes) {
      int channel = oldNote.getChannel();
      if (channel == Midi.DRUM) {
        newSong.add(oldNote);
      } else {
        int oldMidiNote = oldNote.getMidiNote();
        int newMidiNote = oldMidiNote + distance;
        if (newMidiNote >= 0 && newMidiNote < Midi.NOTES) {
          Note newNote = new NoteBuilder().withNote(oldNote).withMidiNote(newMidiNote).create();
          newSong.add(newNote);
        }
      }
    }
  }
}
