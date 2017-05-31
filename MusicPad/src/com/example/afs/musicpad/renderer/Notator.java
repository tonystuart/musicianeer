// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.example.afs.musicpad.device.qwerty.Device;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.Chord;
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

public class Notator {

  public static class KeyCap {
    private String keyCap;
    private Slice slice;

    public KeyCap(Slice slice, String keyCap) {
      this.slice = slice;
      this.keyCap = keyCap;
    }

    public String getKeyCap() {
      return keyCap;
    }

    public Slice getSlice() {
      return slice;
    }

    public long getTick() {
      return slice.getTick();
    }
  }

  public static class Slice implements Iterable<Note> {
    private SortedSet<Note> notes;
    private long tick;
    private Note first;
    private Chord chord;

    public Slice(SortedSet<Note> notes) {
      this.notes = notes;
      this.first = notes.first();
      this.tick = first.getTick();
      this.chord = new Chord(notes);
    }

    public Note first() {
      return first;
    }

    public Chord getChord() {
      return chord;
    }

    public String getName() {
      return chord.getChordType().getName();
    }

    public long getTick() {
      return tick;
    }

    @Override
    public Iterator<Note> iterator() {
      return notes.iterator();
    }

    public int size() {
      return notes.size();
    }
  }

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

  private static final long RESOLUTION = Default.TICKS_PER_BEAT / 2;

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
  private Device device;

  public Notator(Song song, int channel, int ticksPerPixel, Device device) {
    this.song = song;
    this.channel = channel;
    this.ticksPerPixel = ticksPerPixel;
    this.device = device;
  }

  public String getMusic() {
    String music = "No music for channel " + Value.toNumber(channel);
    if (song.getChannelNoteCount(channel) > 0) {
      long duration = song.getDuration();
      Svg staff = getStaff(duration);
      drawMeasures(staff, duration);
      long tick = 0;
      RandomAccessList<Slice> slices = new DirectList<>();
      TreeSet<Note> notes = song.getNotes(channel);
      while (tick < duration) {
        SortedSet<Note> subSet = notes.subSet(new Note(tick), new Note(tick + RESOLUTION));
        if (subSet.size() > 0) {
          Slice slice = new Slice(subSet);
          slices.add(slice);
          drawSlice(staff, slice);
        }
        tick += RESOLUTION;
      }
      drawNoteNames(staff, slices);
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

  private void drawNoteNames(Svg staff, RandomAccessList<Slice> slices) {
    RandomAccessList<KeyCap> keyCaps = device.toKeyCaps(slices);
    for (KeyCap keyCap : keyCaps) {
      int wordX = getX(keyCap.getTick() - RADIUS); // align with left edge of note head
      Slice slice = keyCap.getSlice();
      String name = slice.getName();
      staff.add(new Text(wordX, WORDS + 3 * RADIUS, keyCap.getKeyCap()));
      staff.add(new Text(wordX, 3 * RADIUS, name));
    }
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

  private void drawSlice(Svg staff, Slice slice) {
    RandomAccessList<Note> trebleNotes = new DirectList<>();
    RandomAccessList<Note> bassNotes = new DirectList<>();
    for (Note note : slice) {
      int midiNote = note.getMidiNote();
      if (midiNote < MIDDLE) {
        bassNotes.add(note);
      } else {
        trebleNotes.add(note);
      }
    }
    drawNotes(staff, slice.getTick(), trebleNotes, TREBLE_MIDI_NOTES[2]);
    drawNotes(staff, slice.getTick(), bassNotes, BASS_MIDI_NOTES[2]);
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

  private Svg getStaff(long duration) {
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
