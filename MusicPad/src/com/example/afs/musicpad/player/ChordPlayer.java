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
import com.example.afs.musicpad.player.PrompterData.BrowserMusic;
import com.example.afs.musicpad.player.PrompterData.BrowserWords;
import com.example.afs.musicpad.song.Chord;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.theory.ChordType;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class ChordPlayer extends Player {

  private TreeSet<Chord> chords;
  private RandomAccessList<ChordType> chordMapping;
  private Map<ChordType, Integer> reverseChordMapping;

  public ChordPlayer(Synthesizer synthesizer, Song song, Device device) {
    super(synthesizer, song, device);
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
  }

  @Override
  public PrompterData getPrompterData() {
    List<BrowserWords> words = getWords();
    List<BrowserMusic> music = new LinkedList<>();
    for (Chord chord : chords) {
      ChordType chordType = chord.getChordType();
      long tick = chord.getTick();
      int duration = (int) chord.getDuration();
      int index = reverseChordMapping.get(chordType);
      int midiNote = 48 + index;
      BrowserMusic browserMusic = new BrowserMusic(tick, midiNote, duration);
      music.add(browserMusic);
    }
    int channel = device.getChannel();
    int lowest = song.getLowestMidiNote(channel);
    int highest = song.getHighestMidiNote(channel);
    String[] legend = getLegend(lowest, highest);
    PrompterData prompterData = new PrompterData(song, device, legend, lowest, highest, words, music);
    return prompterData;
  }

  @Override
  public void play(Action action, int midiNote) {
    int index = 48 + midiNote;
    ChordType chordType = chordMapping.get(index);
    int octave = Default.OCTAVE_SEMITONE;
    playMidiChord(action, octave, chordType);
  }

}
