// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.midi;

import java.util.HashSet;
import java.util.Set;

import com.example.afs.musicianeer.song.ChannelNotes;
import com.example.afs.musicianeer.song.Note;
import com.example.afs.musicianeer.song.Song;

public class ChannelInfoFactory {

  public static class ChannelInfo {

    private int concurrency;
    private int occupancy;
    private int percentMeasuresPlayed;
    private int percentMelody;
    private String programNames;
    private int noteCount;
    private int[] noteCountsByMeasure;

    public int getConcurrency() {
      return concurrency;
    }

    public int getNoteCount() {
      return noteCount;
    }

    public int[] getNoteCountsByMeasure() {
      return noteCountsByMeasure;
    }

    public int getOccupancy() {
      return occupancy;
    }

    public int getPercentMeasuresPlayed() {
      return percentMeasuresPlayed;
    }

    public int getPercentMelody() {
      return percentMelody;
    }

    public String getProgramNames() {
      return programNames;
    }

    @Override
    public String toString() {
      return "ChannelInfo [concurrency=" + concurrency + ", occupancy=" + occupancy + ", percentMeasuresPlayed=" + percentMeasuresPlayed + ", percentMelody=" + percentMelody + ", programNames=" + programNames + ", noteCount=" + noteCount + "]";
    }

  }

  private Song song;

  public ChannelInfoFactory(Song song) {
    this.song = song;
  }

  public ChannelInfo getChannelInfo(int channel) {
    ChannelInfo channelInfo = new ChannelInfo();
    channelInfo.concurrency = song.getConcurrency(channel);
    channelInfo.occupancy = song.getOccupancy(channel);
    channelInfo.percentMeasuresPlayed = getPercentMeasuresPlayed(channel);
    channelInfo.percentMelody = song.getPercentMelody(channel);
    channelInfo.programNames = getProgramNames(channel);
    channelInfo.noteCount = song.getTotalDistinctNoteCount(channel);
    channelInfo.noteCountsByMeasure = song.getNoteCountsByMeasure(channel);
    return channelInfo;
  }

  private int getPercentMeasuresPlayed(int channel) {
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

  private String getProgramNames(int channel) {
    StringBuilder s = new StringBuilder();
    for (String programName : song.getProgramNames(channel)) {
      if (s.length() > 0) {
        s.append(", ");
      }
      s.append(programName);
    }
    return s.toString();
  }

}
