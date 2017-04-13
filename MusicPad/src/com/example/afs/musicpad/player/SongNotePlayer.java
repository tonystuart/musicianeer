// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.device.common.InputMapping;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;

public class SongNotePlayer extends SongPlayer {

  private int[] noteIndexToNote;
  private Map<Integer, String> noteToKeySequence;
  private TreeSet<Note> notes;

  public SongNotePlayer(Synthesizer synthesizer, Song song, int channel, InputMapping inputMapping) {
    super(synthesizer, song, channel);
    notes = song.getNotes(channel);
    noteIndexToNote = getUniqueMidiNotes();
    noteToKeySequence = new HashMap<>();
    System.out.println("Total notes: " + notes.size() + ", Unique notes: " + noteIndexToNote.length);
    updateInputDevice(inputMapping);
    setTitle("Channel " + (channel + 1) + " Notes");
  }

  @Override
  public void play(Action action, int noteIndex) {
    if (noteIndex < noteIndexToNote.length) {
      int midiNote = noteIndexToNote[noteIndex];
      playMidiNote(action, midiNote);
    }
  }

  @Override
  public void updateInputDevice(InputMapping inputMapping) {
    for (int noteIndex = 0; noteIndex < noteIndexToNote.length; noteIndex++) {
      int midiNote = noteIndexToNote[noteIndex];
      String keySequence = inputMapping.fromIndexToSequence(noteIndex);
      noteToKeySequence.put(midiNote, keySequence);
      System.out.println(keySequence + " -> " + midiNote);
    }
  }

  @Override
  protected String getMusic(long currentTick, long firstTick, long lastTick, int ticksPerCharacter) {
    StringBuilder s = new StringBuilder();
    long untilTick;
    Note previousNote = notes.lower(new Note(currentTick));
    if (previousNote == null) {
      untilTick = 0;
    } else {
      untilTick = previousNote.getTick() + previousNote.getDuration();
    }
    for (long tick = firstTick; tick < lastTick; tick += ticksPerCharacter) {
      long nextTick = tick + ticksPerCharacter;
      if (currentTick >= tick && currentTick < nextTick) {
        s.append(">");
      } else {
        s.append(" ");
      }
      SortedSet<Note> tickNotes = notes.subSet(new Note(tick), new Note(nextTick));
      int noteCount = tickNotes.size();
      if (noteCount == 0) {
        if (tick < untilTick) {
          s.append("~");
        } else {
          s.append(".");
        }
      } else {
        if (noteCount > 1) {
          //System.out.println("Squeezing " + noteCount + " notes into space for one note");
        }
        for (Note note : tickNotes) {
          int midiNote = note.getMidiNote();
          String keySequence = noteToKeySequence.get(midiNote);
          if (keySequence.length() > 1) {
            //System.out.println("Squeezing " + keySequence.length() + " characters into space for one character");
          }
          s.append(keySequence);
          untilTick = note.getTick() + note.getDuration();
        }
      }
    }
    return s.toString();
  }

  private int[] getUniqueMidiNotes() {
    Set<Integer> uniqueNotes = new TreeSet<>();
    for (Note note : notes) {
      int midiNote = note.getMidiNote();
      uniqueNotes.add(midiNote);
    }
    int noteIndex = 0;
    int uniqueNoteCount = uniqueNotes.size();
    int[] numberToNote = new int[uniqueNoteCount];
    for (int midiNote : uniqueNotes) {
      numberToNote[noteIndex] = midiNote;
      noteIndex++;
    }
    return numberToNote;
  }

}
