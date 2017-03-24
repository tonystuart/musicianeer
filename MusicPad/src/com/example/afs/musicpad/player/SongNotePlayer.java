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
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.device.CharCode;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Contour;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;

public class SongNotePlayer extends SongPlayer {

  private int[] buttonIndexToNote;
  private Map<Integer, String> noteToKeySequence;
  private TreeSet<Contour> contours;

  public SongNotePlayer(Synthesizer synthesizer, Song song, int channel) {
    super(synthesizer, song, channel);
    contours = getContours(song, channel);
    buttonIndexToNote = getUniqueMidiNotes();
    noteToKeySequence = new HashMap<>();
    System.out.println("Total notes: " + contours.size() + ", Unique notes: " + buttonIndexToNote.length);
    for (int buttonIndex = 0; buttonIndex < buttonIndexToNote.length; buttonIndex++) {
      int midiNote = buttonIndexToNote[buttonIndex];
      String keySequence = CharCode.fromIndexToSequence(buttonIndex);
      noteToKeySequence.put(midiNote, keySequence);
      System.out.println(keySequence + " -> " + midiNote);
    }
    setTitle("Channel " + (channel + 1) + " Notes");
  }

  @Override
  public int getUniqueCount() {
    return buttonIndexToNote.length;
  }

  @Override
  public void play(Action action, int noteIndex) {
    if (noteIndex < buttonIndexToNote.length) {
      int midiNote = buttonIndexToNote[noteIndex];
      playMidiNote(action, midiNote);
    }
  }

  @Override
  protected String getMusic(long currentTick, long firstTick, long lastTick, int ticksPerCharacter) {
    StringBuilder s = new StringBuilder();
    NavigableSet<Contour> tickContours = contours.subSet(new Contour(firstTick), false, new Contour(lastTick), true);
    if (tickContours.size() > 0) {
      Contour first = tickContours.first();
      long previousTick = first.getTick();
      long firstContourTick = first.getTick();
      s.append(getIntroTicks(firstTick, firstContourTick));
      for (Contour contour : tickContours) {
        long contourTick = contour.getTick();
        long measureTick = song.roundTickToThisMeasure(contourTick);
        if (measureTick > previousTick && measureTick <= contourTick) {
          s.append("|");
        }
        while (((contourTick - previousTick) / Default.TICKS_PER_BEAT) > 0) {
          s.append(".");
          previousTick += Default.TICKS_PER_BEAT;
        }
        int midiNote = contour.getMidiNote();
        String keySequence = noteToKeySequence.get(midiNote);
        //s.append(Names.formatNoteName(midiNote) + " (" + keySequence + ") ");
        s.append(keySequence + "   ");
        previousTick = contourTick;
      }
    }
    return s.toString();
  }

  private TreeSet<Contour> getContours(Song song, int channel) {
    TreeSet<Contour> contours;
    if (channel == Midi.MELODIC) {
      contours = new TreeSet<>();
      for (int i = 0; i < Midi.CHANNELS; i++) {
        if (i != Midi.DRUM) {
          contours.addAll(song.getContours(i));
        }
      }
    } else {
      contours = song.getContours(channel);
    }
    return contours;
  }

  private int[] getUniqueMidiNotes() {
    Set<Integer> uniqueNotes = new TreeSet<>();
    for (Contour contour : contours) {
      int midiNote = contour.getMidiNote();
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
