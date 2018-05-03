// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.afs.musicpad.analyzer.KeyScore;
import com.example.afs.musicpad.analyzer.KeySignatures;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.PlayableMap.OutputType;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.player.Sounds;
import com.example.afs.musicpad.player.Sounds.SoundCount;
import com.example.afs.musicpad.song.ChannelNotes;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;

public class SongInfo {

  private Song song;
  private int easyTransposition;

  public SongInfo(CurrentSong currentSong) {
    this.song = currentSong.getSong();
    this.easyTransposition = currentSong.getEasyTransposition();
  }

  public int[] getActiveChannels() {
    return song.getActiveChannels();
  }

  public int getBeatsPerMeasure(long tick) {
    return song.getBeatsPerMeasure(tick);
  }

  public int getBeatsPerMinute(long tick) {
    return song.getBeatsPerMinute(tick);
  }

  public int getBeatUnit(long tick) {
    return song.getBeatUnit(tick);
  }

  public double getComplexity() {
    double complexity = 0;
    long duration = song.getDuration();
    long measures = duration / song.getTicksPerMeasure(0);
    if (measures > 0) {
      int noteCount = song.getNoteCount();
      complexity = noteCount / measures;
    }
    return complexity;
  }

  public int getConcurrency(int channel) {
    return song.getConcurrency(channel);
  }

  public String getDuration() {
    int secondsDuration = song.getSeconds();
    String duration = String.format("%d:%02d", secondsDuration / 60, secondsDuration % 60);
    return duration;
  }

  public int getEasyTransposition() {
    return easyTransposition;
  }

  public int getOccupancy(int channel) {
    return song.getOccupancy(channel);
  }

  public int getParts() {
    return song.getActiveChannelCount();
  }

  public int getPercentMeasuresPlayed(int channel) {
    Set<Integer> measures = new HashSet<>();
    ChannelNotes notes = new ChannelNotes(song.getNotes(), channel);
    for (Note note : notes) {
      measures.add(note.getMeasure());
    }
    int totalMeasures = (int) (song.getDuration() / song.getTicksPerMeasure(0));
    int percentMeasuresPlayed = 0;
    if (totalMeasures != 0) {
      percentMeasuresPlayed = (100 * measures.size()) / totalMeasures;
    }
    return percentMeasuresPlayed;
  }

  public int getPercentMelody(int channel) {
    return song.getPercentMelody(channel);
  }

  public String getPredominantKey() {
    String key = "";
    StringBuilder s = new StringBuilder();
    int[] noteCounts = new int[Midi.SEMITONES_PER_OCTAVE];
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (song.getChannelNoteCount(channel) > 0) {
        if (channel != Midi.DRUM) {
          int[] channelNoteCounts = song.getChromaticNoteCounts(channel);
          for (int i = 0; i < noteCounts.length; i++) {
            noteCounts[i] += channelNoteCounts[i];
          }
        }
      }
    }
    KeyScore[] keyScores = KeySignatures.getKeyScores(noteCounts);
    for (int i = 0; i < keyScores.length && s.length() == 0; i++) {
      KeyScore keyScore = keyScores[i];
      int rank = keyScore.getRank();
      if (rank == 1) {
        key = keyScore.getKey();
      }
    }
    return key;
  }

  public String getProgramNames(int channel) {
    StringBuilder s = new StringBuilder();
    for (String programName : song.getProgramNames(channel)) {
      if (s.length() > 0) {
        s.append(", ");
      }
      s.append(programName);
    }
    return s.toString();
  }

  public String getTimeSignature() {
    return song.getBeatsPerMeasure(0) + "/" + song.getBeatUnit(0);
  }

  public String getTitle() {
    return song.getTitle();
  }

  public int getUniqueSounds(int channel) {
    ChannelNotes notes = new ChannelNotes(song.getNotes(), channel);
    Sounds sounds = new Sounds(OutputType.TICK, notes);
    Map<Sound, SoundCount> uniqueSoundCounts = sounds.getUniqueSoundCounts();
    return uniqueSoundCounts.size();
  }

}
