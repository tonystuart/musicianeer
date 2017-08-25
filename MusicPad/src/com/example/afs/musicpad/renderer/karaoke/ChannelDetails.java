// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.afs.musicpad.device.common.DeviceHandler.OutputType;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.player.Sounds;
import com.example.afs.musicpad.player.Sounds.SoundCount;
import com.example.afs.musicpad.song.ChannelNotes;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;

public class ChannelDetails {

  private int channel;

  private Song song;

  public ChannelDetails(Song song, int channel) {
    this.song = song;
    this.channel = channel;
  }

  public String render() {
    Division division = new Division();
    division.appendChild(Utils.createPair("Title", song.getTitle()));
    division.appendChild(Utils.createPair("Instruments", getProgramNames()));
    division.appendChild(Utils.createPair("Percent of Measures Played", getPercentMeasuresPlayed() + "%"));
    division.appendChild(Utils.createPair("Percent of Time Tracking Melody", ""));
    division.appendChild(Utils.createPair("Percent of Time Playing", song.getOccupancy(channel) + "%"));
    division.appendChild(Utils.createPair("Average Number of Notes Playing at Once", (double) (song.getConcurrency(channel) / 100)));
    division.appendChild(Utils.createPair("Unique Sounds", getUniqueSounds()));
    String html = division.render();
    return html;
  }

  private int getPercentMeasuresPlayed() {
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

  private String getProgramNames() {
    StringBuilder s = new StringBuilder();
    for (String programName : song.getProgramNames(channel)) {
      if (s.length() > 0) {
        s.append(", ");
      }
      s.append(programName);
    }
    return s.toString();
  }

  private int getUniqueSounds() {
    ChannelNotes notes = new ChannelNotes(song.getNotes(), channel);
    Sounds sounds = new Sounds(OutputType.TICK, notes);
    Map<Sound, SoundCount> uniqueSoundCounts = sounds.getUniqueSoundCounts();
    return uniqueSoundCounts.size();
  }

}
