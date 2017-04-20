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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Trace;
import com.example.afs.musicpad.analyzer.ChordFinder;
import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.device.common.InputMapping;
import com.example.afs.musicpad.device.midi.MidiMapping;
import com.example.afs.musicpad.device.qwerty.AlphaMapping;
import com.example.afs.musicpad.device.qwerty.NumericMapping;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.player.WordsAndMusic.BrowserMusic;
import com.example.afs.musicpad.player.WordsAndMusic.BrowserWords;
import com.example.afs.musicpad.song.Chord;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.theory.ChordType;
import com.example.afs.musicpad.theory.Keys;
import com.example.afs.musicpad.theory.ScaleBasedChordTypes;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Velocity;

public class SuperPlayer {

  public enum KeyType {
    GENERIC, SONG
  }

  public enum MappingType {
    PIANO, ALPHA, NUMERIC
  }

  public enum UnitType {
    NOTE, CHORD
  }

  public static final int PLAYER_BASE = Midi.CHANNELS;
  public static final int PLAYER_CHANNELS = Midi.CHANNELS;
  public static final int TOTAL_CHANNELS = PLAYER_BASE + PLAYER_CHANNELS;

  public static final int DEFAULT_VELOCITY = 96;
  public static final int DEFAULT_PERCENT_VELOCITY = 100;

  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;

  private Synthesizer synthesizer;
  private Song song;
  private int channel;
  private MappingType mappingType;
  private KeyType keyType;
  private UnitType unitType;
  private TreeSet<Chord> chords;
  private RandomAccessList<Integer> noteMapping;
  private RandomAccessList<ChordType> chordMapping;
  private InputMapping inputMapping;
  private ScaleBasedChordTypes scaleBasedChordTypes;
  private Map<Integer, Integer> reverseNoteMapping;
  private Map<ChordType, Integer> reverseChordMapping;

  public SuperPlayer(Synthesizer synthesizer, Song song, int channel, MappingType mappingType, KeyType keyType, UnitType unitType) {
    this.synthesizer = synthesizer;
    this.song = song;
    this.channel = channel;
    this.mappingType = mappingType;
    this.keyType = keyType;
    this.unitType = unitType;

    assert song != null;

    if (unitType == UnitType.NOTE) {
      if (keyType == KeyType.SONG) {
        Set<Integer> uniqueMidiNotes = new HashSet<>();
        for (Note note : song.getNotes()) {
          if (note.getChannel() == channel) {
            uniqueMidiNotes.add(note.getMidiNote());
          }
        }
        noteMapping = new DirectList<>(uniqueMidiNotes);
        noteMapping.sort((o1, o2) -> o1.compareTo(o2));
        reverseNoteMapping = new HashMap<>();
        for (int i = 0; i < noteMapping.size(); i++) {
          reverseNoteMapping.put(noteMapping.get(i), i);
        }
      } else {
        // calculated in player
      }
    } else {
      if (keyType == KeyType.SONG) {
        ChordFinder chordFinder = new ChordFinder();
        chords = chordFinder.getChords(song.getNotes(), channel);
        Set<ChordType> uniqueChordTypes = new HashSet<>();
        for (Chord chord : chords) {
          uniqueChordTypes.add(chord.getChordType());
        }
        chordMapping = new DirectList<>(uniqueChordTypes);
        chordMapping.sort((o1, o2) -> o1.compareTo(o2));
        reverseChordMapping = new HashMap<>();
        for (int i = 0; i < chordMapping.size(); i++) {
          reverseChordMapping.put(chordMapping.get(i), i);
        }
      } else {
        scaleBasedChordTypes = new ScaleBasedChordTypes(Keys.CMajor);
        // calculated in player
      }
    }

    if (mappingType == MappingType.ALPHA) {
      inputMapping = new AlphaMapping();
    } else if (mappingType == MappingType.NUMERIC) {
      inputMapping = new NumericMapping();
    } else if (mappingType == MappingType.PIANO) {
      inputMapping = new MidiMapping();
    }
  }

  public WordsAndMusic getWordsAndMusic() {
    WordsAndMusic wordsAndMusic = null;
    int lowest = Midi.NOTES;
    int highest = 0;
    List<BrowserWords> words = getWords();
    List<BrowserMusic> music = new LinkedList<>();
    if (unitType == UnitType.NOTE) {
      if (keyType == KeyType.SONG) {
        for (Note note : song.getNotes()) {
          if (note.getChannel() == channel) {
            int midiNote = note.getMidiNote();
            long tick = note.getTick();
            int duration = (int) note.getDuration();
            int index = reverseNoteMapping.get(midiNote);
            int inputCode = inputMapping.fromNoteIndex(index);
            BrowserMusic browserMusic = new BrowserMusic(tick, inputCode, duration);
            music.add(browserMusic);
            if (index < lowest) {
              lowest = index;
            }
            if (index > highest) {
              highest = index;
            }
          }
        }
      } else {
        for (Note note : song.getNotes()) {
          if (note.getChannel() == channel) {
            int midiNote = note.getMidiNote();
            long tick = note.getTick();
            int duration = (int) note.getDuration();
            int index = midiNote;
            int inputCode = index;
            BrowserMusic browserMusic = new BrowserMusic(tick, inputCode, duration);
            music.add(browserMusic);
            if (index < lowest) {
              lowest = index;
            }
            if (index > highest) {
              highest = index;
            }
          }
        }
      }
      wordsAndMusic = new WordsAndMusic(song.getName(), song.getDuration(), channel, lowest, highest, words, music);
    } else {
      if (keyType == KeyType.SONG) {
        for (Chord chord : chords) {
          ChordType chordType = chord.getChordType();
          long tick = chord.getTick();
          int duration = (int) chord.getDuration();
          int index = reverseChordMapping.get(chordType);
          int inputCode = inputMapping.fromNoteIndex(index);
          BrowserMusic browserMusic = new BrowserMusic(tick, inputCode, duration);
          music.add(browserMusic);
          if (index < lowest) {
            lowest = index;
          }
          if (index > highest) {
            highest = index;
          }
        }
      } else {
        // TODO: use scale based chords
      }
    }
    return wordsAndMusic;
  }

  public void play(Action action, int inputCode) {
    int noteIndex = inputMapping.toNoteIndex(inputCode);
    if (unitType == UnitType.NOTE) {
      int midiNote;
      if (keyType == KeyType.SONG && song != null) {
        midiNote = noteMapping.get(noteIndex);
      } else {
        midiNote = Keys.CMajor.getMidiNotes()[noteIndex];
      }
      playMidiNote(action, midiNote);
    } else if (unitType == UnitType.CHORD) {
      int octave;
      ChordType chordType;
      if (keyType == KeyType.SONG && song != null) {
        chordType = chordMapping.get(noteIndex);
        octave = Default.OCTAVE_SEMITONE;
      } else {
        int degree = noteIndex % Midi.NOTES_PER_OCTAVE;
        octave = (noteIndex / Midi.NOTES_PER_OCTAVE) * Midi.SEMITONES_PER_OCTAVE;
        chordType = scaleBasedChordTypes.get(degree);
      }
      playMidiChord(action, octave, chordType);
    }
  }

  public void selectProgram(int program) {
    synthesizer.changeProgram(PLAYER_BASE + channel, program);
  }

  public void setPercentVelocity(int percentVelocity) {
    this.percentVelocity = percentVelocity;
  }

  private LinkedList<BrowserWords> getWords() {
    LinkedList<BrowserWords> words = new LinkedList<>();
    for (Word word : song.getWords()) {
      BrowserWords browserWords = new BrowserWords(word.getTick(), word.getText());
      words.add(browserWords);
    }
    return words;
  }

  private void playMidiChord(Action action, int octave, ChordType chordType) {
    if (action == Action.PRESS && Trace.isTracePlay()) {
      System.out.println("Player.play: chordType=" + chordType);
    }
    for (int midiNote : chordType.getMidiNotes()) {
      try {
        synthesizeNote(action, octave + midiNote);
        Thread.sleep(0);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void playMidiNote(Action action, int midiNote) {
    if (action == Action.PRESS && Trace.isTracePlay()) {
      System.out.println("Player.play: midiNote=" + Names.formatNote(midiNote));
    }
    synthesizeNote(action, midiNote);
  }

  private void synthesizeNote(Action action, int midiNote) {
    switch (action) {
    case PRESS:
      synthesizer.pressKey(PLAYER_BASE + channel, midiNote, Velocity.scale(DEFAULT_VELOCITY, percentVelocity));
      break;
    case RELEASE:
      synthesizer.releaseKey(PLAYER_BASE + channel, midiNote);
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

}
