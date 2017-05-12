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
import com.example.afs.musicpad.svg.Line;
import com.example.afs.musicpad.svg.Svg;
import com.example.afs.musicpad.svg.Text;

public class NotePlayer extends Player {

  private static final int LOWEST = 24;
  private static final int HIGHEST = 96;
  private static final int MIDDLE = 60;

  // http://www.theoreticallycorrect.com/Helmholtz-Pitch-Numbering/
  private static final int[] TREBLE_MIDI_NOTES = new int[] {
      64,
      67,
      71,
      74,
      77
  };

  private static final int[] BASS_MIDI_NOTES = new int[] {
      43,
      47,
      50,
      53,
      57
  };

  private static final int[] LEDGER_MIDI_NOTES = new int[] {
      26,
      27,
      29,
      30,
      33,
      34,
      36,
      37,
      40,
      60,
      61,
      81,
      82,
      84,
      85,
      88,
      91,
      92,
      95
  };

  private static final int[] POSITION = createPosition();
  private static final boolean[] LEDGER = createLedger();

  private static final int RADIUS = 10; // spacing is r, diameter is 2r
  private static final int LEDGER_WIDTH = RADIUS * 2;
  private static final int INTER_CLEF = RADIUS * 5;

  private static final int TOP = RADIUS * 1;
  private static final int FIRST = TOP;
  private static final int SPAN = (POSITION[HIGHEST] - POSITION[LOWEST]) + 1;
  private static final int LAST = FIRST + (SPAN * RADIUS) + INTER_CLEF;
  private static final int BOTTOM = LAST + RADIUS * 1;
  private static final int WORDS = LAST / 2;

  private static boolean[] createLedger() {
    boolean[] ledger = new boolean[Midi.NOTES];
    for (int ledgerNote = 0; ledgerNote < LEDGER_MIDI_NOTES.length; ledgerNote++) {
      ledger[LEDGER_MIDI_NOTES[ledgerNote]] = true;
    }
    return ledger;
  }

  private static int[] createPosition() {
    int position = 0;
    int[] positions = new int[Midi.NOTES];
    for (int midiNote = LOWEST; midiNote <= HIGHEST; midiNote++) {
      positions[midiNote] = position;
      if (!Names.isSharp(midiNote + 1)) {
        position++;
      }
    }
    return positions;
  }

  public NotePlayer(DeviceHandler deviceHandler, Song song) {
    super(deviceHandler, song);
    initializeOctave();
  }

  @Override
  public OnMusic getOnSongMusic() {

    for (int i = LOWEST; i <= HIGHEST; i++) {
      int y = getY(i);
      System.out.println("midiNote=" + i + ", y=" + y);
    }
    System.out.println("WORDS=" + WORDS);

    int width = scale((int) song.getDuration());
    Svg svg = new Svg(width, BOTTOM);

    for (int i = 0; i < TREBLE_MIDI_NOTES.length; i++) {
      int y = getY(TREBLE_MIDI_NOTES[i]);
      System.out.println("i=" + i + ", midiNote=" + TREBLE_MIDI_NOTES[i] + ", y=" + y);
      svg.add(new Line(0, y, width, y));
    }

    for (int i = 0; i < BASS_MIDI_NOTES.length; i++) {
      int y = getY(BASS_MIDI_NOTES[i]);
      System.out.println("i=" + i + ", midiNote=" + BASS_MIDI_NOTES[i] + ", y=" + y);
      svg.add(new Line(0, y, width, y));
    }

    long lastTick = 0;

    for (Note note : song.getNotes(songChannel)) {
      int midiNote = note.getMidiNote();
      long noteTick = note.getTick();
      int noteX = scale(noteTick);
      int noteY = getY(midiNote);
      if (LEDGER[midiNote]) {
        svg.add(new Line(noteX - LEDGER_WIDTH, noteY, noteX + LEDGER_WIDTH, noteY));
      }
      svg.add(new Circle(noteX, noteY, RADIUS));
      SortedSet<Word> words = song.getWords().subSet(new Word(lastTick), new Word((int) noteTick));
      for (Word word : words) {
        long wordTick = word.getTick();
        int wordX = scale(wordTick);
        svg.add(new Text(wordX, WORDS, word.getText()));
      }
      String keyCap = inputMapping.toKeyCap(midiNote);
      boolean isSharp = Names.isSharp(midiNote);
      if (isSharp) {

      }
      svg.add(new Text(noteX, BOTTOM, keyCap));
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

  private int getY(int midiNote) {
    int y;
    if (midiNote < MIDDLE) {
      y = LAST - (POSITION[midiNote] * RADIUS);
    } else {
      y = FIRST + ((POSITION[HIGHEST] - POSITION[midiNote]) * RADIUS);
    }
    return y;
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

  private int scale(long wordTick) {
    return (int) (wordTick / 10);
  }

}
