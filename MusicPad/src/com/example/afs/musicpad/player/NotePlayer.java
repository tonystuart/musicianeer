// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.song.Contour;
import com.example.afs.musicpad.song.Song;

public class NotePlayer extends Player {

  private int[] numberToNote;
  private Map<Integer, String> noteToNumber;
  private TreeSet<Contour> contours;

  public NotePlayer(Synthesizer synthesizer, Song song, int channel) {
    super(synthesizer, song, channel);
    contours = song.getContours(channel);
    Set<Integer> uniqueNotes = new HashSet<>();
    for (Contour contour : contours) {
      int midiNote = contour.getMidiNote();
      uniqueNotes.add(midiNote);
    }
    int noteIndex = 0;
    int uniqueNoteCount = uniqueNotes.size();
    numberToNote = new int[uniqueNoteCount];
    for (int midiNote : uniqueNotes) {
      numberToNote[noteIndex] = midiNote;
      noteIndex++;
    }
    Arrays.sort(numberToNote);
    noteToNumber = new HashMap<>();
    for (int i = 0; i < numberToNote.length; i++) {
      noteToNumber.put(numberToNote[i], Integer.toString(i));
    }
    System.out.println("Total notes: " + contours.size() + ", Unique notes: " + uniqueNotes.size());
    for (int i = 0; i < numberToNote.length; i++) {
      System.out.println(i + " -> " + Names.getNoteName(numberToNote[i]));
    }
    setTitle("Channel " + (channel + 1) + " Notes");
  }

  @Override
  public int getUniqueCount() {
    return numberToNote.length;
  }

  @Override
  public void play(Action action, int digit) {
    int index = page * ITEMS_PER_PAGE + digit;
    if (index < numberToNote.length) {
      int midiNote = numberToNote[index];
      playMidiNote(action, midiNote);
    }
  }

  @Override
  protected String getMusic(long firstTick, long lastTick) {
    StringBuilder s = new StringBuilder();
    NavigableSet<Contour> tickContours = contours.subSet(new Contour(firstTick), false, new Contour(lastTick), true);
    if (tickContours.size() > 0) {
      Contour first = tickContours.first();
      long firstContourTick = first.getTick();
      s.append(getIntroTicks(firstTick, firstContourTick));
      for (Contour contour : tickContours) {
        int midiNote = contour.getMidiNote();
        String number = noteToNumber.get(midiNote);
        s.append(Names.formatNoteName(midiNote) + " (" + number + ") ");
      }
    }
    return s.toString();
  }

}
