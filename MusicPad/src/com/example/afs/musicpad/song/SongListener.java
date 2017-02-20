// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

import java.util.NavigableMap;
import java.util.TreeMap;

import com.example.afs.musicpad.song.MidiParser.Listener;

public class SongListener implements Listener {

  public static class Tempo {
    private int usecPerQuarterNote;
    private int quarterNotesPerMinute;

    public Tempo(int usecPerQuarterNote, int quarterNotesPerMinute) {
      this.usecPerQuarterNote = usecPerQuarterNote;
      this.quarterNotesPerMinute = quarterNotesPerMinute;
    }

    public int getQuarterNotesPerMinute() {
      return quarterNotesPerMinute;
    }

    public int getUsecPerQuarterNote() {
      return usecPerQuarterNote;
    }

    @Override
    public String toString() {
      return "Tempo [usecPerQuarterNote=" + usecPerQuarterNote + ", quarterNotesPerMinute=" + quarterNotesPerMinute + "]";
    }
  }

  public static class TimeSignature {
    private int beatsPerMeasure;
    private int beatUnit;

    public TimeSignature(int beatsPerMeasure, int beatUnit) {
      this.beatsPerMeasure = beatsPerMeasure;
      this.beatUnit = beatUnit;
    }

    public int getBeatsPerMeasure() {
      return beatsPerMeasure;
    }

    public int getBeatUnit() {
      return beatUnit;
    }

    @Override
    public String toString() {
      return "TimeSignature [beatsPerMeasure=" + beatsPerMeasure + ", beatUnit=" + beatUnit + "]";
    }
  }

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
  public void onLyrics(long tick, String lyrics) {
    song.add(new Lyric(tick, lyrics));
  }

  @Override
  public void onNote(long tick, int channel, int note, int velocity, long duration, int instrument, int group) {
    Tempo tempo = tempos.floorEntry(tick).getValue();
    TimeSignature timeSignature = timeSignatures.floorEntry(tick).getValue();
    song.add(new Note(tick, channel, note, velocity, duration, instrument, group, tempo.getQuarterNotesPerMinute(), timeSignature.getBeatsPerMeasure(), timeSignature.getBeatUnit()));
  }

  @Override
  public void onTempoChange(long tick, int usecPerQuarterNote, int quarterNotesPerMinute) {
    tempos.put(tick, new Tempo(usecPerQuarterNote, quarterNotesPerMinute));
  }

  @Override
  public void onText(long tick, String text) {
    song.add(new Text(tick, text));
  }

  @Override
  public void onTimeSignatureChange(long tick, int beatsPerMeasure, int beatUnit) {
    timeSignatures.put(tick, new TimeSignature(beatsPerMeasure, beatUnit));
  }

  @Override
  public String toString() {
    return "SongListener [song=" + song + ", tempos=" + tempos + ", timeSignatures=" + timeSignatures + "]";
  }

}
