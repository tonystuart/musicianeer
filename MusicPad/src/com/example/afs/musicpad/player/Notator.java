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
import java.util.TreeSet;

import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.svg.Circle;
import com.example.afs.musicpad.svg.Line;
import com.example.afs.musicpad.svg.Svg;
import com.example.afs.musicpad.svg.Text;
import com.example.afs.musicpad.theory.ChordType;
import com.example.afs.musicpad.theory.IntervalSet;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Notator {

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

  private static final boolean[] SHARPS = new boolean[] {
      false,
      true,
      false,
      true,
      false,
      false,
      true,
      false,
      true,
      false,
      true,
      false,
  };
  private static final int[] POSITION = createPosition();
  private static final boolean[] LEDGER = createLedger();

  private static final int RADIUS = 10; // spacing is r, diameter is 2r
  private static final int LEDGER_WIDTH = RADIUS * 2;
  private static final int INTER_CLEF = RADIUS * 6;

  private static final int TOP = RADIUS * 1;
  private static final int SPAN = (POSITION[HIGHEST] - POSITION[LOWEST]) + 1;
  private static final int BOTTOM = TOP + (SPAN * RADIUS) + INTER_CLEF;
  private static final int WORDS = BOTTOM / 2;
  private static final long RESOLUTION = Default.TICKS_PER_BEAT / 2;

  public static boolean isSharp(int midiNote) {
    return SHARPS[midiNote % SHARPS.length];
  }

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
      if (!isSharp(midiNote + 1)) {
        position++;
      }
    }
    return positions;
  }

  private Song song;
  private int channel;
  private Player player;

  public Notator(Player player, Song song, int channel) {
    this.player = player;
    this.song = song;
    this.channel = channel;
  }

  public String getMusic() {

    if (song.getChannelNoteCount(channel) == 0) {
      return "";
    }

    int top;
    int highestMidiNote = song.getHighestMidiNote();
    if (highestMidiNote < MIDDLE) {
      top = getY(MIDDLE) + RADIUS;
    } else {
      top = getY(Math.max(highestMidiNote, TREBLE_MIDI_NOTES[4])) - RADIUS;
    }

    int bottom;
    int lowestMidiNote = song.getLowestMidiNote();
    if (lowestMidiNote < MIDDLE) {
      bottom = getY(Math.min(lowestMidiNote, BASS_MIDI_NOTES[0])) + RADIUS;
    } else {
      bottom = getY(MIDDLE) + INTER_CLEF;
    }

    long duration = song.getDuration();
    int width = scale(duration);
    Svg svg = new Svg(0, top, width, bottom);

    for (int i = 0; i < TREBLE_MIDI_NOTES.length; i++) {
      int y = getY(TREBLE_MIDI_NOTES[i]);
      svg.add(new Line(0, y, width, y));
    }

    for (int i = 0; i < BASS_MIDI_NOTES.length; i++) {
      int y = getY(BASS_MIDI_NOTES[i]);
      svg.add(new Line(0, y, width, y));
    }

    long tick = 0;
    while (tick < duration) {
      int x = scale(tick);
      if (tick > 0) {
        tick -= 2 * RADIUS; // so note doesn't land on it
      }
      svg.add(new Line(x, getY(TREBLE_MIDI_NOTES[4]), x, getY(BASS_MIDI_NOTES[0])));
      tick += song.getTicksPerMeasure(tick);
    }

    tick = 0;
    TreeSet<Note> notes = song.getNotes(channel);
    while (tick < duration) {
      int midiNote = 0;
      long firstTick = -1;
      IntervalSet intervalSet = new IntervalSet();
      SortedSet<Note> slice = notes.subSet(new Note(tick), new Note(tick + RESOLUTION));
      int sliceNoteCount = slice.size();
      if (sliceNoteCount > 0) {
        firstTick = slice.first().getTick();
        RandomAccessList<Note> trebleNotes = new DirectList<>();
        RandomAccessList<Note> bassNotes = new DirectList<>();
        for (Note note : slice) {
          midiNote = note.getMidiNote();
          if (midiNote < MIDDLE) {
            bassNotes.add(note);
          } else {
            trebleNotes.add(note);
          }
          intervalSet.add(note);
        }
        plotNotes(svg, firstTick, trebleNotes, TREBLE_MIDI_NOTES[2]);
        plotNotes(svg, firstTick, bassNotes, BASS_MIDI_NOTES[2]);
      }
      if (sliceNoteCount == 1) {
        String keyCap = player.toKeyCap(midiNote);
        svg.add(new Text(scale(firstTick), WORDS + 3 * RADIUS, keyCap));
      } else if (sliceNoteCount > 1) {
        ChordType chordType = intervalSet.getChordType();
        String keyCap = player.toKeyCap(chordType);
        svg.add(new Text(scale(firstTick), WORDS + 3 * RADIUS, keyCap));
      }
      tick += RESOLUTION;
    }
    SortedSet<Word> words = song.getWords();
    for (Word word : words) {
      long wordTick = word.getTick();
      int wordX = scale(wordTick);
      svg.add(new Text(wordX, WORDS, formatText(word.getText())));
    }
    String music = svg.render();
    return music;
  }

  private String formatText(String text) {
    if (text.startsWith("\\") || text.startsWith("/")) {
      text = text.substring(1);
    }
    if (text.length() > 10) {
      text = text.substring(0, 10);
    }
    return text;
  }

  private int getY(int midiNote) {
    int y;
    if (midiNote < MIDDLE) {
      y = BOTTOM - (POSITION[midiNote] * RADIUS);
    } else {
      y = TOP + ((POSITION[HIGHEST] - POSITION[midiNote]) * RADIUS);
    }
    return y;
  }

  private void plotNotes(Svg svg, long firstTick, RandomAccessList<Note> notes, int midPoint) {
    int lowestMidiNote = Midi.MAX_VALUE;
    int highestMidiNote = 0;
    long totalDuration = 0;
    for (Note note : notes) {
      int midiNote = note.getMidiNote();
      lowestMidiNote = Math.min(midiNote, lowestMidiNote);
      highestMidiNote = Math.max(midiNote, highestMidiNote);
      int noteX = scale(firstTick);
      int noteY = getY(midiNote);
      if (LEDGER[midiNote]) {
        svg.add(new Line(noteX - LEDGER_WIDTH, noteY, noteX + LEDGER_WIDTH, noteY));
      }
      boolean isSharp = isSharp(midiNote);
      if (isSharp) {
        int left = noteX - 4 * RADIUS;
        int right = noteX - 2 * RADIUS;
        int top = noteY - RADIUS / 2;
        int bottom = noteY + RADIUS / 2;
        svg.add(new Line(left, top, right, top));
        svg.add(new Line(left, bottom, right, bottom));
        svg.add(new Line(left + RADIUS / 2, top - RADIUS / 2, left + RADIUS / 2, bottom + RADIUS / 2));
        svg.add(new Line(left + 3 * RADIUS / 2, top - RADIUS / 2, left + 3 * RADIUS / 2, bottom + RADIUS / 2));
      }
      long duration = note.getDuration();
      totalDuration += duration;
      if (duration < 1200) {
        svg.add(new Circle(noteX, noteY, RADIUS, true));
      } else {
        svg.add(new Circle(noteX, noteY, RADIUS, false));
      }
    }
    if (notes.size() > 0) {
      long averageDuration = totalDuration / notes.size();
      if (averageDuration < 1900) {
        if (lowestMidiNote < midPoint) {
          int x = scale(firstTick) + RADIUS;
          svg.add(new Line(x, getY(highestMidiNote) - 5 * RADIUS, x, getY(lowestMidiNote)));
          if (averageDuration < 600) {
            svg.add(new Line(x, getY(highestMidiNote) - 5 * RADIUS, x + RADIUS, getY(highestMidiNote) - 3 * RADIUS));
          }
        } else {
          int x = scale(firstTick) - RADIUS;
          svg.add(new Line(x, getY(highestMidiNote), x, getY(lowestMidiNote) + 5 * RADIUS));
        }
      }
    }
  }

  private int scale(long wordTick) {
    return (int) (wordTick / 10);
  }

}
