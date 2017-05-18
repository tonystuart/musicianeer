// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer;

import com.example.afs.musicpad.analyzer.KeyScore;
import com.example.afs.musicpad.analyzer.KeySignatures;
import com.example.afs.musicpad.analyzer.TranspositionFinder;
import com.example.afs.musicpad.html.CheckBox;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Select;
import com.example.afs.musicpad.html.Table;
import com.example.afs.musicpad.html.TableHeader;
import com.example.afs.musicpad.html.TableRow;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;

public class HeaderRenderer {

  private Song song;

  public HeaderRenderer(Song song) {
    this.song = song;
  }

  public String render() {
    Division detail = new Division();
    detail.setId("header-detail");

    Table table = new Table();
    TableHeader header = table.createHeader();

    header.append("Title");
    header.append("Master Override");
    header.append("Key (chromatics / triads / thirds)");
    header.append("Transpose to White");
    header.append("Current Transposition");
    header.append("BPM");
    header.append("Time");
    header.append("Duration");

    TableRow row = table.createRow();
    row.append(new Select("title"));
    row.append(new CheckBox("master-override"));
    row.append(getKeyInfo(song));
    row.append(TranspositionFinder.getDistanceToWhiteKeys(song));
    row.append(song.getTransposition());
    row.append(song.getBeatsPerMinute(0));
    row.append(song.getBeatsPerMeasure(0) + "/" + song.getBeatUnit(0));
    row.append(getDuration());

    detail.appendChild(table);

    StringBuilder s = new StringBuilder();
    detail.render(s);
    return s.toString();
  }

  private String getDuration() {
    long tickDuration = song.getDuration();
    int beatsPerMinute = song.getBeatsPerMinute(0);
    int beatsPerSecond = beatsPerMinute * 60;
    long secondsDuration = tickDuration / beatsPerSecond;
    String duration = String.format("%d:%02d", secondsDuration / 60, secondsDuration % 60);
    return duration;
  }

  private String getKeyInfo(Song song) {
    StringBuilder s = new StringBuilder();
    int[] noteCounts = new int[Midi.SEMITONES_PER_OCTAVE];
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (song.getChannelNoteCount(channel) > 0) {
        if (channel != Midi.DRUM) {
          int[] channelNoteCounts = song.getCommonNoteCounts(channel);
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
        String key = keyScore.getKey();
        String synopsis = keyScore.getSynopsis();
        int accidentals = keyScore.getAccidentals();
        int triads = keyScore.getTriads();
        int thirds = keyScore.getThirds();
        s.append(String.format("%s (%s) %d / %d / %d", key, synopsis, accidentals, triads, thirds));
      }
    }
    return s.toString();
  }

}
