// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.SortedSet;

import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.message.OnMusic;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.svg.Circle;
import com.example.afs.musicpad.svg.Svg;
import com.example.afs.musicpad.svg.Text;

public class NotePlayer extends Player {

  private static final int RADIUS = 10;

  private static final String NOTE = "note";

  public NotePlayer(DeviceHandler deviceHandler, Song song) {
    super(deviceHandler, song);
    initializeOctave();
  }

  @Override
  public OnMusic getOnSongMusic() {
    int lastTick = 0;
    int lowest = getLowestMidiNote();
    int highest = getHighestMidiNote();
    int range = highest - lowest;
    int width = (int) song.getDuration();
    int height = ((highest - lowest) + 2) * RADIUS;
    Svg svg = new Svg(width, height);
    for (Note note : song.getNotes(songChannel)) {
      int noteTick = (int) note.getTick();
      int scaledNoteTick = scale(noteTick);
      int midiNote = note.getMidiNote();
      int x = scaledNoteTick;
      int a = midiNote - lowest;
      int b = range - a;
      int y = b * RADIUS;
      svg.add(new Circle(x, y, RADIUS));
      SortedSet<Word> words = song.getWords().subSet(new Word(lastTick), new Word(noteTick));
      for (Word word : words) {
        int wordTick = (int) word.getTick();
        int scaledWordTick = scale(wordTick);
        svg.add(new Text(scaledWordTick, (range + 1) * RADIUS, word.getText()));
      }
      String keyCap = inputMapping.toKeyCap(midiNote);
      boolean isSharp = Names.isSharp(midiNote);
      svg.add(new Text(scaledNoteTick, (range + 2) * RADIUS, keyCap));
      lastTick = noteTick;
    }
    String music = svg.render();
    OnMusic onMusic = new OnMusic(index, music);
    return onMusic;
  }

  @Override
  public void play(Action action, int midiNote) {
    playMidiNote(action, midiNote);
  }

  private void initializeOctave() {
    int octave = inputMapping.getDefaultOctave();
    int lowestMidiNote = getLowestMidiNote();
    int lowestOctave = lowestMidiNote / Midi.SEMITONES_PER_OCTAVE;
    if (lowestOctave < octave) {
      octave = lowestOctave;
    }
    inputMapping.setOctave(octave);
  }

  private int scale(int wordTick) {
    return wordTick / 10;
  }

}
