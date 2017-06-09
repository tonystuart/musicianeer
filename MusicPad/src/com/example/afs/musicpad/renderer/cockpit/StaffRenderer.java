// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.cockpit;

import java.util.SortedSet;

import com.example.afs.musicpad.keycap.KeyCap;
import com.example.afs.musicpad.keycap.KeyCapMap;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.svg.Circle;
import com.example.afs.musicpad.svg.Line;
import com.example.afs.musicpad.svg.Svg;
import com.example.afs.musicpad.svg.Text;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Value;

public class StaffRenderer {

  private static class Context {
    private NoteType noteType;
    private Stem stem;
    private int lowestMidiNote;
    private int highestMidiNote;

    public Context(NoteType noteType, Stem stem, int lowestMidiNote, int highestMidiNote) {
      this.noteType = noteType;
      this.stem = stem;
      this.lowestMidiNote = lowestMidiNote;
      this.highestMidiNote = highestMidiNote;
    }

    public int getHighestMidiNote() {
      return highestMidiNote;
    }

    public int getLowestMidiNote() {
      return lowestMidiNote;
    }

    public NoteType getNoteType() {
      return noteType;
    }

    public Stem getStem() {
      return stem;
    }

    @Override
    public String toString() {
      return "Context [duration=" + noteType + ", stem=" + stem + ", lowestMidiNote=" + lowestMidiNote + ", highestMidiNote=" + highestMidiNote + "]";
    }

  }

  private enum NoteType {
    EIGHTH, QUARTER, HALF, WHOLE
  }

  private enum Stem {
    UP, DOWN
  }

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

  private static final String CLOSED = "closed";
  private static final String OPEN = "open";

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
  private int ticksPerPixel;
  private KeyCapMap keyCapMap;

  public StaffRenderer(Song song, int channel, int ticksPerPixel, KeyCapMap keyCapMap) {
    this.song = song;
    this.channel = channel;
    this.ticksPerPixel = ticksPerPixel;
    this.keyCapMap = keyCapMap;
  }

  public String getMusic() {
    String music = "No music for channel " + Value.toNumber(channel);
    if (song.getChannelNoteCount(channel) > 0) {
      long duration = song.getDuration();
      Svg staff = drawStaff(duration);
      drawMeasures(staff, duration);
      drawNotes(staff);
      drawNoteNames(staff);
      drawWords(staff);
      music = staff.render();
    }
    return music;
  }

  private void drawHead(Svg staff, Context context, int noteX, int noteY) {
    switch (context.getNoteType()) {
    case EIGHTH:
    case QUARTER:
      staff.add(new Circle(noteX, noteY, RADIUS, CLOSED));
      break;
    case HALF:
    case WHOLE:
      staff.add(new Circle(noteX, noteY, RADIUS, OPEN));
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private void drawMeasures(Svg staff, long duration) {
    long tick = 0;
    while (tick < duration) {
      int x = getX(tick);
      if (tick > 0) {
        tick -= 2 * RADIUS; // so note doesn't land on it
      }
      staff.add(new Line(x, getY(TREBLE_MIDI_NOTES[4]), x, getY(BASS_MIDI_NOTES[0])));
      tick += song.getTicksPerMeasure(tick);
    }
  }

  private void drawNoteNames(Svg staff) {
    RandomAccessList<KeyCap> keyCaps = keyCapMap.getKeyCaps();
    for (KeyCap keyCap : keyCaps) {
      int wordX = getX(keyCap.getTick() - RADIUS); // align with left edge of note head
      Sound sound = keyCap.getSound();
      String name = sound.getName();
      staff.add(new Text(wordX, WORDS + 3 * RADIUS, keyCap.getLegend()));
      staff.add(new Text(wordX, 3 * RADIUS, name));
    }
  }

  private void drawNotes(Svg staff) {
    int lastStartIndex = -1;
    Sound sound = null;
    for (Note note : song.getNotes()) {
      if (note.getChannel() == channel) {
        int startIndex = note.getStartIndex();
        if (startIndex != lastStartIndex) {
          lastStartIndex = startIndex;
          if (sound != null) {
            drawSound(staff, sound);
          }
          sound = new Sound();
        }
        sound.add(note);
      }
    }
    drawSound(staff, sound);
  }

  private void drawNotes(Svg staff, long firstTick, RandomAccessList<Note> notes, int midPoint) {
    if (notes.size() > 0) {
      Context context = getContext(notes, midPoint);
      for (Note note : notes) {
        int midiNote = note.getMidiNote();
        int noteX = getX(firstTick);
        int noteY = getY(midiNote);
        if (LEDGER[midiNote]) {
          staff.add(new Line(noteX - LEDGER_WIDTH, noteY, noteX + LEDGER_WIDTH, noteY));
        }
        boolean isSharp = isSharp(midiNote);
        if (isSharp) {
          drawSharp(staff, noteX, noteY);
        }
        drawHead(staff, context, noteX, noteY);
      }
      drawStem(staff, firstTick, context);
    }
  }

  private void drawSharp(Svg staff, int noteX, int y) {
    int full = RADIUS;
    int half = RADIUS / 2;
    int x = noteX - 3 * RADIUS;
    staff.add(new Line(x - full, y - half, x + full, y - half));
    staff.add(new Line(x - full, y + half, x + full, y + half));
    staff.add(new Line(x - half, y - full, x - half, y + full));
    staff.add(new Line(x + half, y - full, x + half, y + full));
  }

  private void drawSound(Svg staff, Sound sound) {
    RandomAccessList<Note> trebleNotes = new DirectList<>();
    RandomAccessList<Note> bassNotes = new DirectList<>();
    for (Note note : sound) {
      int midiNote = note.getMidiNote();
      if (midiNote < MIDDLE) {
        bassNotes.add(note);
      } else {
        trebleNotes.add(note);
      }
    }
    drawNotes(staff, sound.getTick(), trebleNotes, TREBLE_MIDI_NOTES[2]);
    drawNotes(staff, sound.getTick(), bassNotes, BASS_MIDI_NOTES[2]);
  }

  private Svg drawStaff(long duration) {
    int bottom = getY(POSITION[LOWEST]);
    int width = getX(duration);
    Svg staff = new Svg(0, 0, width, bottom);
    for (int i = 0; i < TREBLE_MIDI_NOTES.length; i++) {
      int y = getY(TREBLE_MIDI_NOTES[i]);
      staff.add(new Line(0, y, width, y));
    }
    for (int i = 0; i < BASS_MIDI_NOTES.length; i++) {
      int y = getY(BASS_MIDI_NOTES[i]);
      staff.add(new Line(0, y, width, y));
    }
    return staff;
  }

  private void drawStem(Svg staff, long firstTick, Context context) {
    if (context.getNoteType() != NoteType.WHOLE) {
      int noteTop = getY(context.getHighestMidiNote());
      int noteBottom = getY(context.getLowestMidiNote());
      int stemLength = 5 * RADIUS;
      int flagLength = 2 * RADIUS;
      if (context.getStem() == Stem.UP) {
        int x = getX(firstTick) + RADIUS;
        int stemTop = noteTop - stemLength;
        staff.add(new Line(x, stemTop, x, noteBottom));
        if (context.getNoteType() == NoteType.EIGHTH) {
          staff.add(new Line(x, stemTop, x + RADIUS, stemTop + flagLength));
        }
      } else {
        int x = getX(firstTick) - RADIUS;
        int stemBottom = noteBottom + stemLength;
        staff.add(new Line(x, noteTop, x, stemBottom));
        if (context.getNoteType() == NoteType.EIGHTH) {
          staff.add(new Line(x, stemBottom, x - RADIUS, stemBottom - flagLength));
        }
      }
    }
  }

  private void drawWords(Svg staff) {
    SortedSet<Word> words = song.getWords();
    for (Word word : words) {
      long tick = word.getTick();
      long wordTick = tick - RADIUS; // align with left edge of note head
      int wordX = getX(wordTick);
      staff.add(new Text(wordX, WORDS, formatText(word.getText())));
    }
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

  private Context getContext(RandomAccessList<Note> notes, int midPoint) {
    NoteType noteType;
    int lowestMidiNote = Midi.MAX_VALUE;
    int highestMidiNote = 0;
    long totalDuration = 0;
    for (Note note : notes) {
      totalDuration += note.getDuration();
      int midiNote = note.getMidiNote();
      lowestMidiNote = Math.min(midiNote, lowestMidiNote);
      highestMidiNote = Math.max(midiNote, highestMidiNote);
    }
    Stem stem;
    if (lowestMidiNote < midPoint) {
      stem = Stem.UP;
    } else {
      stem = Stem.DOWN;
    }
    long averageDuration = totalDuration / notes.size();
    if (averageDuration < Default.TICKS_PER_BEAT / 2 + Default.GAP_BEAT_UNIT) {
      noteType = NoteType.EIGHTH;
    } else if (averageDuration < Default.TICKS_PER_BEAT + Default.GAP_BEAT_UNIT) {
      noteType = NoteType.QUARTER;
    } else if (averageDuration < Default.TICKS_PER_BEAT * 2 + Default.GAP_BEAT_UNIT) {
      noteType = NoteType.HALF;
    } else {
      noteType = NoteType.WHOLE;
    }
    Context context = new Context(noteType, stem, lowestMidiNote, highestMidiNote);
    return context;
  }

  private int getX(long tick) {
    return (int) (tick / ticksPerPixel);
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

}
