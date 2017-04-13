// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.parser;

import java.util.NavigableMap;
import java.util.TreeMap;

import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;

public class SongListener implements Listener {

  private static final Tempo DEFAULT_TEMPO = new Tempo(60000000 / Default.BEATS_PER_MINUTE, Default.BEATS_PER_MINUTE);
  private static final TimeSignature DEFAULT_TIME_SIGNATURE = new TimeSignature(Default.BEATS_PER_MEASURE, Default.BEAT_UNIT);

  private Song song;
  private NavigableMap<Long, Tempo> tempos = new TreeMap<>();
  private NavigableMap<Long, TimeSignature> timeSignatures = new TreeMap<>();

  public SongListener(Song song) {
    this.song = song;
    tempos.put(-1L, DEFAULT_TEMPO);
    timeSignatures.put(-1L, DEFAULT_TIME_SIGNATURE);
  }

  @Override
  public void onBegin(String fileName) {
  }

  @Override
  public void onConcurrency(int channel, int concurrency) {
    song.setConcurrency(channel, concurrency);
  }

  @Override
  public void onEnd(String fileName) {
  }

  @Override
  public void onLyrics(long tick, String text) {
    addWord(tick, text);
  }

  @Override
  public void onNote(long tick, int channel, int midiNote, int velocity, long duration, int program, int group) {
    Tempo tempo = tempos.floorEntry(tick).getValue();
    TimeSignature timeSignature = timeSignatures.floorEntry(tick).getValue();
    song.add(new Note(tick, channel, midiNote, velocity, duration, program, group, tempo.getQuarterNotesPerMinute(), timeSignature.getBeatsPerMeasure(), timeSignature.getBeatUnit()));
  }

  @Override
  public void onOccupancy(int channel, int occupancy) {
    song.setOccupancy(channel, occupancy);
  }

  @Override
  public void onTempoChange(long tick, int usecPerQuarterNote, int quarterNotesPerMinute) {
    tempos.put(tick, new Tempo(usecPerQuarterNote, quarterNotesPerMinute));
  }

  @Override
  public void onText(long tick, String text) {
    addWord(tick, text);
  }

  @Override
  public void onTimeSignatureChange(long tick, int beatsPerMeasure, int beatUnit) {
    timeSignatures.put(tick, new TimeSignature(beatsPerMeasure, beatUnit));
  }

  @Override
  public String toString() {
    return "SongListener [song=" + song + ", tempos=" + tempos + ", timeSignatures=" + timeSignatures + "]";
  }

  private void addWord(long tick, String text) {
    song.add(new Word(tick, text));
  }

}
