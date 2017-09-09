// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import com.example.afs.musicpad.analyzer.KeyScore;
import com.example.afs.musicpad.analyzer.KeySignatures;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;

public class SongDetails {

  private Song song;

  public SongDetails(Song song) {
    this.song = song;
  }

  public String render() {
    Division division = new Division();
    division.appendChild(Utils.createPair("Title", song.getTitle()));
    division.appendChild(Utils.createPair("Duration", getDuration()));
    division.appendChild(Utils.createPair("Parts", song.getActiveChannelCount()));
    division.appendChild(Utils.createPair("Beats per Minute", song.getBeatsPerMinute(0)));
    division.appendChild(Utils.createPair("Time Signature", song.getBeatsPerMeasure(0) + "/" + song.getBeatUnit(0)));
    division.appendChild(Utils.createPair("Predominant Key", getKeyInfo()));
    division.appendChild(Utils.createPair("EZ Keyboard Transposition", song.getDistanceToWhiteKeys()));
    division.appendChild(Utils.createPair("Complexity", getComplexity()));
    String html = division.render();
    return html;
  }

  private double getComplexity() {
    double complexity = 0;
    long duration = song.getDuration();
    long measures = duration / song.getTicksPerMeasure(0);
    if (measures > 0) {
      int noteCount = song.getNoteCount();
      complexity = noteCount / measures;
    }
    return complexity;
  }

  private String getDuration() {
    long tickDuration = song.getDuration();
    long beatDuration = tickDuration / Default.TICKS_PER_BEAT;
    int beatsPerMinute = song.getBeatsPerMinute(0);
    int secondsDuration = (int) ((60 * beatDuration) / beatsPerMinute);
    String duration = String.format("%d:%02d", secondsDuration / 60, secondsDuration % 60);
    return duration;
  }

  private String getKeyInfo() {
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
