// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.midi;

import java.io.File;
import java.util.Arrays;

import com.example.afs.musicianeer.analyzer.KeyScore;
import com.example.afs.musicianeer.analyzer.KeySignatures;
import com.example.afs.musicpad.song.Song;

public class SongInfoFactory {

  public class SongInfo {
    private int[] activeChannels;
    private int beatsPerMinute;
    private double complexity;
    private String duration;
    private int easyTransposition;
    private String predominantKey;
    private long timeLastModified;
    private String timeSignature;
    private String title;

    public int[] getActiveChannels() {
      return activeChannels;
    }

    public int getBeatsPerMinute() {
      return beatsPerMinute;
    }

    public double getComplexity() {
      return complexity;
    }

    public String getDuration() {
      return duration;
    }

    public int getEasyTransposition() {
      return easyTransposition;
    }

    public String getPredominantKey() {
      return predominantKey;
    }

    public long getTimeLastModified() {
      return timeLastModified;
    }

    public String getTimeSignature() {
      return timeSignature;
    }

    public String getTitle() {
      return title;
    }

    @Override
    public String toString() {
      return "SongInfo [activeChannels=" + Arrays.toString(activeChannels) + ", beatsPerMinute=" + beatsPerMinute + ", complexity=" + complexity + ", duration=" + duration + ", easyTransposition=" + easyTransposition + ", predominantKey=" + predominantKey + ", timeLastModified=" + timeLastModified + ", timeSignature=" + timeSignature + ", title=" + title + "]";
    }

  }

  private MidiLibrary midiLibrary;

  public SongInfoFactory(MidiLibrary midiLibrary) {
    this.midiLibrary = midiLibrary;
  }

  public double getComplexity(Song song) {
    double complexity = 0;
    long duration = song.getDuration();
    long measures = duration / song.getTicksPerMeasure(0);
    if (measures > 0) {
      int noteCount = song.getNoteCount();
      complexity = noteCount / measures;
    }
    return complexity;
  }

  public String getDuration(Song song) {
    int secondsDuration = song.getSeconds();
    String duration = String.format("%d:%02d", secondsDuration / 60, secondsDuration % 60);
    return duration;
  }

  public String getPredominantKey(Song song) {
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

  public SongInfo getSongInfo(File midiFile) {
    SongInfo songInfo = new SongInfo();
    Song song = midiLibrary.readSong(midiFile);
    songInfo.activeChannels = song.getActiveChannels();
    songInfo.beatsPerMinute = song.getBeatsPerMinute(0);
    songInfo.complexity = getComplexity(song);
    songInfo.duration = getDuration(song);
    songInfo.easyTransposition = getEasyTransposition(song);
    songInfo.predominantKey = getPredominantKey(song);
    songInfo.timeLastModified = midiFile.lastModified();
    songInfo.timeSignature = song.getBeatsPerMeasure(0) + "/" + song.getBeatUnit(0);
    songInfo.title = song.getTitle();
    return songInfo;
  }

  private int getEasyTransposition(Song song) {
    int distanceToWhiteKeys = song.getDistanceToWhiteKeys();
    int easyTransposition = 0;
    if (distanceToWhiteKeys < 0) {
      int minimumTransposition = song.getMinimumTransposition();
      if (Math.abs(distanceToWhiteKeys) < Math.abs(minimumTransposition)) {
        easyTransposition = distanceToWhiteKeys;
      }
    } else if (distanceToWhiteKeys > 0) {
      int maximumTransposition = song.getMaximumTransposition();
      if (distanceToWhiteKeys < maximumTransposition) {
        easyTransposition = distanceToWhiteKeys;
      }
    }
    return easyTransposition;
  }

}
