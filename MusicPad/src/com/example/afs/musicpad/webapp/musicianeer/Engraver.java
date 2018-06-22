// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import java.util.SortedSet;

import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.PlayableMap.OutputType;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.player.Sounds;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.svg.Ellipse;
import com.example.afs.musicpad.svg.Line;
import com.example.afs.musicpad.svg.Path;
import com.example.afs.musicpad.svg.Svg;
import com.example.afs.musicpad.svg.Svg.Type;
import com.example.afs.musicpad.svg.Text;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Engraver {

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
    SIXTEENTH, EIGHTH, QUARTER, HALF, WHOLE, DRUM
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

  // NB: when descending draw ledger line for sharp of next lowest note
  private static final int[] BASS_LEDGER_MIDI_NOTES = new int[] {
      40,
      37,
      34,
      30,
      27,
  };

  private static final int[] TREBLE_LEDGER_MIDI_NOTES = new int[] {
      81,
      84,
      88,
      91,
      95,
  };

  private static final int[] MIDDLE_LEDGER_MIDI_NOTES = new int[] {
      61,
      60,
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
  private static final int X_RADIUS = 12; // spacing is r, diameter is 2r
  private static final int Y_RADIUS = 10; // spacing is r, diameter is 2r
  private static final int LEDGER_WIDTH = X_RADIUS * 2;
  private static final int INTER_CLEF = Y_RADIUS * 6;
  private static final int DRUM_HEAD = Y_RADIUS / 2;

  private static final int TOP = Y_RADIUS * 1;
  private static final int SPAN = (POSITION[HIGHEST] - POSITION[LOWEST]) + 1;
  private static final int BOTTOM = TOP + (SPAN * Y_RADIUS) + INTER_CLEF;
  private static final int WORDS = BOTTOM / 2;

  private static final String CLOSED = "closed";
  private static final String OPEN = "open";

  public static final int TICKS_PER_PIXEL = 5;

  private static final int FLAG_LENGTH = (7 * Y_RADIUS) / 2;
  private static final int FLAG_CY1 = (2 * FLAG_LENGTH) / 4;
  private static final int FLAG_CY2 = (3 * FLAG_LENGTH) / 4;
  private static final int FLAG_Y2 = (4 * FLAG_LENGTH) / 4;
  private static final int FLAG_CX1 = (1 * X_RADIUS) / 4;
  private static final int FLAG_CX2 = (8 * X_RADIUS) / 4;
  private static final int FLAG_X2 = (3 * X_RADIUS) / 4;

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

  private static boolean isSharp(int midiNote) {
    return SHARPS[midiNote % SHARPS.length];
  }

  public Parent notate(Song song, int channel, int transposition) {
    return getStaff(song, channel, transposition);
  }

  private void draw16thNoteFlag(Svg staff, Stem stem, int x1, int y1) {
    switch (stem) {
    case UP:
      staff.add(new Path().moveTo(x1, y1) //
          .setRelative(true) //
          .curveTo(FLAG_CX1, FLAG_CY1, FLAG_CX2, FLAG_CY2, FLAG_X2, FLAG_Y2));
      staff.add(new Path().moveTo(x1, y1 + 2 * Y_RADIUS) //
          .setRelative(true) //
          .curveTo(FLAG_CX1, FLAG_CY1, FLAG_CX2, FLAG_CY2, FLAG_X2, FLAG_Y2));
      break;
    case DOWN:
      staff.add(new Path().moveTo(x1, y1) //
          .setRelative(true) //
          .curveTo(FLAG_CX1, -FLAG_CY1, FLAG_CX2, -FLAG_CY2, FLAG_X2, -FLAG_Y2));
      staff.add(new Path().moveTo(x1, y1 - 2 * Y_RADIUS) //
          .setRelative(true) //
          .curveTo(FLAG_CX1, -FLAG_CY1, FLAG_CX2, -FLAG_CY2, FLAG_X2, -FLAG_Y2));
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private void draw8thNoteFlag(Svg staff, Stem stem, int x1, int y1) {
    switch (stem) {
    case UP:
      staff.add(new Path().moveTo(x1, y1) //
          .setRelative(true) //
          .curveTo(FLAG_CX1, FLAG_CY1, FLAG_CX2, FLAG_CY2, FLAG_X2, FLAG_Y2));
      break;
    case DOWN:
      staff.add(new Path().moveTo(x1, y1) //
          .setRelative(true) //
          .curveTo(FLAG_CX1, -FLAG_CY1, FLAG_CX2, -FLAG_CY2, FLAG_X2, -FLAG_Y2));
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private void drawDrumNames(Svg staff, Sounds sounds) {
    for (Sound sound : sounds) {
      int wordX = getX(sound.getBeginTick() - X_RADIUS); // align with left edge of note head
      RandomAccessList<Note> drums = sound.getNotes();
      int drumCount = drums.size();
      for (int i = 0; i < drumCount; i++) {
        Note drum = drums.get(i);
        String drumName = Instruments.getShortDrumName(drum.getMidiNote());
        staff.add(new Text(wordX, 10 * Y_RADIUS - (i * (2 * Y_RADIUS)), drumName));
      }
    }
  }

  private void drawHead(Svg staff, Context context, int noteX, int noteY) {
    switch (context.getNoteType()) {
    case SIXTEENTH:
    case EIGHTH:
    case QUARTER:
      staff.add(new Ellipse(noteX, noteY, X_RADIUS, Y_RADIUS, CLOSED));
      break;
    case HALF:
    case WHOLE:
      staff.add(new Ellipse(noteX, noteY, X_RADIUS, Y_RADIUS, OPEN));
      break;
    case DRUM:
      staff.add(new Line(noteX - DRUM_HEAD, noteY + DRUM_HEAD, noteX + DRUM_HEAD, noteY - DRUM_HEAD));
      staff.add(new Line(noteX - DRUM_HEAD, noteY - DRUM_HEAD, noteX + DRUM_HEAD, noteY + DRUM_HEAD));
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private void drawLedgerLine(Svg staff, int noteX, int ledgerY) {
    staff.add(new Line(noteX - LEDGER_WIDTH, ledgerY, noteX + LEDGER_WIDTH, ledgerY));
  }

  private void drawLedgerLines(int[] ledgerMidiNotes, Svg staff, int midiNote, int noteX) {
    boolean isDescending = ledgerMidiNotes[0] > ledgerMidiNotes[ledgerMidiNotes.length - 1];
    for (int i = 0; i < ledgerMidiNotes.length; i++) {
      int ledgerNote = ledgerMidiNotes[i];
      if (isDescending) {
        if (ledgerNote < midiNote) {
          return;
        }
      } else {
        if (ledgerNote > midiNote) {
          return;
        }
      }
      int ledgerY = getY(ledgerNote);
      drawLedgerLine(staff, noteX, ledgerY);
    }
  }

  private void drawLedgerLines(Svg staff, long firstTick, Context context) {
    int noteX = getX(firstTick);
    int lowestMidiNote = context.getLowestMidiNote();
    if (lowestMidiNote < BASS_MIDI_NOTES[0]) {
      drawLedgerLines(BASS_LEDGER_MIDI_NOTES, staff, lowestMidiNote, noteX);
    }
    int highestMidiNote = context.getHighestMidiNote();
    if (highestMidiNote > TREBLE_MIDI_NOTES[TREBLE_MIDI_NOTES.length - 1]) {
      drawLedgerLines(TREBLE_LEDGER_MIDI_NOTES, staff, highestMidiNote, noteX);
    }
  }

  private void drawMeasures(Song song, Svg staff, long duration) {
    long tick = 0;
    while (tick < duration) {
      int x = getX(tick);
      if (tick > 0) {
        tick -= 2 * X_RADIUS; // so note doesn't land on it
      }
      staff.add(new Line(x, getY(TREBLE_MIDI_NOTES[4]), x, getY(BASS_MIDI_NOTES[0])));
      tick += song.getTicksPerMeasure(tick);
    }
  }

  private void drawNoteNames(Svg staff, Sounds sounds) {
    for (Sound sound : sounds) {
      int wordX = getX(sound.getBeginTick() - X_RADIUS); // align with left edge of note head
      String name = sound.getName();
      staff.add(new Text(wordX, 3 * Y_RADIUS, name));
    }
  }

  private void drawNotes(Svg staff, long firstTick, RandomAccessList<Note> notes, int midPoint) {
    if (notes.size() > 0) {
      Context context = getContext(notes, midPoint);
      drawLedgerLines(staff, firstTick, context);
      for (Note note : notes) {
        int midiNote = note.getMidiNote();
        int noteX = getX(firstTick);
        int noteY = getY(midiNote);
        if (midiNote == MIDDLE_LEDGER_MIDI_NOTES[0] || midiNote == MIDDLE_LEDGER_MIDI_NOTES[1]) {
          drawLedgerLine(staff, noteX, noteY);
        }
        boolean isSharp = isSharp(midiNote);
        if (isSharp) {
          drawSharp(staff, noteX, noteY);
        }
        drawHead(staff, context, noteX, noteY);
      }
      if (context.getNoteType() != NoteType.DRUM) {
        drawStem(staff, firstTick, context);
      }
    }
  }

  private void drawNotes(Svg staff, Sounds sounds) {
    for (Sound sound : sounds) {
      drawSound(staff, sound);
    }
  }

  private void drawSharp(Svg staff, int noteX, int y) {
    int xFull = X_RADIUS;
    int xHalf = X_RADIUS / 2;
    int yFull = Y_RADIUS;
    int yHalf = Y_RADIUS / 2;
    int x = noteX - 3 * X_RADIUS;
    staff.add(new Line(x - xFull, y - yHalf, x + xFull, y - yHalf));
    staff.add(new Line(x - xFull, y + yHalf, x + xFull, y + yHalf));
    staff.add(new Line(x - xHalf, y - yFull, x - xHalf, y + yFull));
    staff.add(new Line(x + xHalf, y - yFull, x + xHalf, y + yFull));
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
    drawNotes(staff, sound.getBeginTick(), trebleNotes, TREBLE_MIDI_NOTES[2]);
    drawNotes(staff, sound.getBeginTick(), bassNotes, BASS_MIDI_NOTES[2]);
  }

  private Svg drawStaff(Song song, int channel, long duration) {
    int bottom = getY(POSITION[LOWEST]);
    int width = getX(duration);
    Svg staff = new Svg(Type.SCALE_TO_FIT, 0, 0, width, bottom, ".channel-" + channel);
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
      int stemLength = 7 * Y_RADIUS;
      if (context.getStem() == Stem.UP) {
        int x = getX(firstTick) + X_RADIUS;
        int stemTop = noteTop - stemLength;
        staff.add(new Line(x, stemTop, x, noteBottom));
        switch (context.getNoteType()) {
        case EIGHTH:
          draw8thNoteFlag(staff, context.stem, x, stemTop);
          break;
        case SIXTEENTH:
          draw16thNoteFlag(staff, context.stem, x, stemTop);
          break;
        default:
          break;
        }
      } else {
        int x = getX(firstTick) - X_RADIUS;
        int stemBottom = noteBottom + stemLength;
        staff.add(new Line(x, noteTop, x, stemBottom));
        switch (context.getNoteType()) {
        case EIGHTH:
          draw8thNoteFlag(staff, context.stem, x, stemBottom);
          break;
        case SIXTEENTH:
          draw16thNoteFlag(staff, context.stem, x, stemBottom);
          break;
        default:
          break;
        }
      }
    }
  }

  private void drawWords(Song song, Svg staff) {
    SortedSet<Word> words = song.getWords();
    for (Word word : words) {
      long tick = word.getTick();
      long wordTick = tick - X_RADIUS; // align with left edge of note head
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
    boolean isDrum = notes.size() > 0 && notes.get(0).getChannel() == Midi.DRUM;
    if (isDrum) {
      noteType = NoteType.DRUM;
    } else if (averageDuration < Default.TICKS_PER_BEAT / 4 + Default.GAP_BEAT_UNIT) {
      noteType = NoteType.SIXTEENTH;
    } else if (averageDuration < Default.TICKS_PER_BEAT / 2 + Default.GAP_BEAT_UNIT) {
      System.out.println("channel=" + notes.get(0).getChannel() + ", tick=" + notes.get(0).getTick() + ", averageDuration=" + averageDuration);
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

  private Svg getStaff(Song song, int channel, int transposition) {
    long duration = song.getDuration();
    Svg staff = drawStaff(song, channel, duration);
    drawMeasures(song, staff, duration);
    Iterable<Note> notes = song.getChannelNotes(channel);
    if (channel == Midi.DRUM) {
      Sounds sounds = new Sounds(OutputType.TICK, notes);
      drawNotes(staff, sounds);
      drawDrumNames(staff, sounds);
    } else {
      Sounds sounds = new Sounds(OutputType.TICK, new Transposer(notes, transposition));
      drawNotes(staff, sounds);
      drawNoteNames(staff, sounds);
    }
    drawWords(song, staff);
    return staff;
  }

  private int getX(long tick) {
    return (int) (tick / TICKS_PER_PIXEL);
  }

  private int getY(int midiNote) {
    int y;
    if (midiNote < MIDDLE) {
      y = BOTTOM - (POSITION[Math.max(midiNote, LOWEST)] * Y_RADIUS);
    } else {
      y = TOP + ((POSITION[HIGHEST] - POSITION[Math.min(midiNote, HIGHEST)]) * Y_RADIUS);
    }
    return y;
  }

}
