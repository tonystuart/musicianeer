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
import java.util.TreeSet;

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
  private Line line;
  private NavigableMap<Long, Tempo> tempos = new TreeMap<>();
  private NavigableMap<Long, TimeSignature> timeSignatures = new TreeMap<>();
  private long lastTick;

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
  public void onContour(int channel, TreeSet<Contour> contour) {
    song.setContour(channel, contour);
  }

  @Override
  public void onEnd(String fileName) {
    processLineEnd();
  }

  @Override
  public void onLyrics(long tick, String text) {
    addWord(tick, text);
  }

  @Override
  public void onNote(long tick, int channel, int note, int velocity, long duration, int instrument, int group) {
    Tempo tempo = tempos.floorEntry(tick).getValue();
    TimeSignature timeSignature = timeSignatures.floorEntry(tick).getValue();
    song.add(new Note(tick, channel, note, velocity, duration, instrument, group, tempo.getQuarterNotesPerMinute(), timeSignature.getBeatsPerMeasure(), timeSignature.getBeatUnit()));
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
    if (text.startsWith("/") || text.startsWith("\\") || tick < lastTick) {
      processLineEnd();
    }
    if (line == null) {
      line = new Line(tick);
    }
    Word word;
    if (tick < lastTick) {
      tick = lastTick + 1;
      word = new Word(tick, "#" + text);
    } else {
      word = new Word(tick, text);
    }
    line.add(word);
    lastTick = tick;
  }

  private void processLineEnd() {
    if (line != null) {
      song.add(line);
      line = null;
    }
  }

}
