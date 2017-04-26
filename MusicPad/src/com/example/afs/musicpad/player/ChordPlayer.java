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
import com.example.afs.musicpad.analyzer.ChordFinder;
import com.example.afs.musicpad.device.common.Device;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.PrompterData.BrowserMusic;
import com.example.afs.musicpad.player.PrompterData.BrowserWords;
import com.example.afs.musicpad.song.Chord;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.theory.ChordType;
import com.example.afs.musicpad.theory.Keys;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class ChordPlayer extends Player {

  private int octave;
  private int baseMidiNote;
  private TreeSet<Chord> chords;
  private Map<ChordType, Integer> chordTypeToMidiNoteIndex = new HashMap<>();
  private Map<Integer, ChordType> midiNoteIndexToChordType = new HashMap<>();

  public ChordPlayer(Synthesizer synthesizer, Song song, Device device) {
    super(synthesizer, song, device);
    octave = device.getInputMapping().getOctave();
    baseMidiNote = octave * Midi.SEMITONES_PER_OCTAVE;
    ChordFinder chordFinder = new ChordFinder();
    chords = chordFinder.getChords(song.getNotes(), device.getChannel());
    Set<ChordType> uniqueChordTypes = new HashSet<>();
    for (Chord chord : chords) {
      uniqueChordTypes.add(chord.getChordType());
    }
    RandomAccessList<ChordType> chordMapping = new DirectList<>(uniqueChordTypes);
    chordMapping.sort((o1, o2) -> o1.compareTo(o2));
    for (int i = 0; i < chordMapping.size(); i++) {
      int midiNoteIndex = Keys.CMajorFull.getMidiNotes()[i];
      chordTypeToMidiNoteIndex.put(chordMapping.get(i), midiNoteIndex);
      midiNoteIndexToChordType.put(midiNoteIndex, chordMapping.get(i));
      System.out.println(chordMapping.get(i) + " <=> " + midiNoteIndex);
    }
  }

  @Override
  public PrompterData getPrompterData() {
    int highest = 0;
    int lowest = Midi.MAX_VALUE;
    List<BrowserWords> words = getWords();
    List<BrowserMusic> music = new LinkedList<>();
    for (Chord chord : chords) {
      ChordType chordType = chord.getChordType();
      long tick = chord.getTick();
      int duration = (int) chord.getDuration();
      int midiNoteIndex = chordTypeToMidiNoteIndex.get(chordType);
      int midiNote = baseMidiNote + midiNoteIndex;
      if (midiNote < lowest) {
        lowest = midiNote;
      }
      if (midiNote > highest) {
        highest = midiNote;
      }
      BrowserMusic browserMusic = new BrowserMusic(tick, midiNote, duration);
      music.add(browserMusic);
    }
    String[] legend = getLegend(lowest, highest);
    PrompterData prompterData = new PrompterData(song, device, legend, lowest, highest, words, music);
    return prompterData;
  }

  @Override
  public void play(Action action, int midiNote) {
    int midiNoteIndex = midiNote - baseMidiNote;
    ChordType chordType = midiNoteIndexToChordType.get(midiNoteIndex);
    if (chordType != null) {
      playMidiChord(action, baseMidiNote + 24, chordType);
    }
  }

}
