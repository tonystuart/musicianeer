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
import com.example.afs.musicpad.device.common.Device;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.PrompterData.BrowserMusic;
import com.example.afs.musicpad.player.PrompterData.BrowserWords;
import com.example.afs.musicpad.song.Chord;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.theory.ChordType;
import com.example.afs.musicpad.theory.ScaleBasedChordTypes;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Velocity;

public class Player {

  public enum Action {
    PRESS, RELEASE
  }

  public enum UnitType {
    NOTE, SCALE_CHORDS, SONG_CHORDS
  }

  public static final int PLAYER_BASE = Midi.CHANNELS;
  public static final int PLAYER_CHANNELS = Midi.CHANNELS;

  public static final int TOTAL_CHANNELS = PLAYER_BASE + PLAYER_CHANNELS;
  public static final int DEFAULT_VELOCITY = 96;

  public static final int DEFAULT_PERCENT_VELOCITY = 100;

  private Song song;
  private Device device;
  private Synthesizer synthesizer;
  private TreeSet<Chord> chords;
  private RandomAccessList<ChordType> chordMapping;
  private Map<ChordType, Integer> reverseChordMapping;
  private ScaleBasedChordTypes scaleBasedChordTypes;
  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;

  public Player(Synthesizer synthesizer, Song song, Device device) {
    this.synthesizer = synthesizer;
    this.song = song;
    this.device = device;

    // TODO: Modify caller to select default channel based on device index

    UnitType unitType = device.getUnitType();
    if (unitType == UnitType.NOTE) {
      if (!isEmptySong()) {
        int lowestMidiNote = song.getLowestMidiNote(device.getChannel());
        int octave = lowestMidiNote / Midi.SEMITONES_PER_OCTAVE;
        device.getInputMapping().setOctave(octave);
      }
    } else if (unitType == UnitType.SCALE_CHORDS || isEmptySong()) {
      // calculated in player
    } else if (unitType == UnitType.SONG_CHORDS) {
      ChordFinder chordFinder = new ChordFinder();
      chords = chordFinder.getChords(song.getNotes(), device.getChannel());
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
      throw new UnsupportedOperationException();
    }

    Set<Integer> programs = song.getPrograms(device.getChannel());
    if (programs.size() > 0) {
      int program = programs.iterator().next();
      synthesizer.changeProgram(PLAYER_BASE + device.getChannel(), program);
    }
  }

  public PrompterData getPrompterData() {
    // TODO: Handle empty song
    PrompterData prompterData = null;
    int lowest = Midi.NOTES;
    int highest = 0;
    List<BrowserWords> words = getWords();
    List<BrowserMusic> music = new LinkedList<>();
    UnitType unitType = device.getUnitType();
    if (unitType == UnitType.NOTE) {
      for (Note note : song.getNotes()) {
        if (note.getChannel() == device.getChannel()) {
          int midiNote = note.getMidiNote();
          long tick = note.getTick();
          int duration = (int) note.getDuration();
          BrowserMusic browserMusic = new BrowserMusic(tick, midiNote, duration);
          music.add(browserMusic);
          // TODO Use lowest/highest from Song
          if (midiNote < lowest) {
            lowest = midiNote;
          }
          if (midiNote > highest) {
            highest = midiNote;
          }
        }
      }
      String[] names = getKeyCaps(lowest, highest);
      prompterData = new PrompterData(song, device, names, lowest, highest, words, music);
    } else if (unitType == UnitType.SCALE_CHORDS || isEmptySong()) {
    } else if (unitType == UnitType.SONG_CHORDS) {
      for (Chord chord : chords) {
        ChordType chordType = chord.getChordType();
        long tick = chord.getTick();
        int duration = (int) chord.getDuration();
        int index = reverseChordMapping.get(chordType);
        int midiNote = 48 + index;
        BrowserMusic browserMusic = new BrowserMusic(tick, midiNote, duration);
        music.add(browserMusic);
        // TODO Use lowest/highest from Song
        if (midiNote < lowest) {
          lowest = midiNote;
        }
        if (midiNote > highest) {
          highest = midiNote;
        }
        String[] names = getKeyCaps(lowest, highest);
        prompterData = new PrompterData(song, device, names, lowest, highest, words, music);
      }
    } else {
      throw new UnsupportedOperationException();
    }
    return prompterData;
  }

  public void play(Action action, int midiNote) {
    UnitType unitType = device.getUnitType();
    if (unitType == UnitType.NOTE) {
      System.out.println("play: midiNote=" + Names.formatNote(midiNote));
      playMidiNote(action, midiNote);
    } else if (unitType == UnitType.SCALE_CHORDS || isEmptySong()) {
      int degree = midiNote % Midi.NOTES_PER_OCTAVE;
      int octave = (midiNote / Midi.NOTES_PER_OCTAVE) * Midi.SEMITONES_PER_OCTAVE;
      ChordType chordType = scaleBasedChordTypes.get(degree);
      playMidiChord(action, octave, chordType);
    } else if (unitType == UnitType.SONG_CHORDS) {
      int index = 48 + midiNote;
      ChordType chordType = chordMapping.get(index);
      int octave = Default.OCTAVE_SEMITONE;
      playMidiChord(action, octave, chordType);
    }
  }

  public void selectProgram(int program) {
    synthesizer.changeProgram(PLAYER_BASE + device.getChannel(), program);
  }

  public void setPercentVelocity(int percentVelocity) {
    this.percentVelocity = percentVelocity;
  }

  private String[] getKeyCaps(int lowest, int highest) {
    int count = (highest - lowest) + 1;
    String[] names = new String[count];
    for (int midiNote = lowest; midiNote <= highest; midiNote++) {
      names[midiNote - lowest] = device.getInputMapping().toKeySequence(midiNote);
    }
    return names;
  }

  private LinkedList<BrowserWords> getWords() {
    LinkedList<BrowserWords> words = new LinkedList<>();
    for (Word word : song.getWords()) {
      BrowserWords browserWords = new BrowserWords(word.getTick(), word.getText());
      words.add(browserWords);
    }
    return words;
  }

  private boolean isEmptySong() {
    return song == null || song.getNotes().size() == 0;
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
      synthesizer.pressKey(PLAYER_BASE + device.getChannel(), midiNote, Velocity.scale(DEFAULT_VELOCITY, percentVelocity));
      break;
    case RELEASE:
      synthesizer.releaseKey(PLAYER_BASE + device.getChannel(), midiNote);
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

}
