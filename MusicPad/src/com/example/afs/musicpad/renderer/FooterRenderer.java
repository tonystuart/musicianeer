// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer;

import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.html.CheckBox;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Table;
import com.example.afs.musicpad.html.TableHeader;
import com.example.afs.musicpad.html.TableRow;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.util.Value;

public class FooterRenderer {

  private Song song;

  public FooterRenderer(Song song) {
    this.song = song;
  }

  public String render() {
    Division detail = new Division();
    detail.setId("footer-detail");

    Table table = new Table();
    TableHeader header = table.createHeader();

    header.append("Channel");
    header.append("Mute");
    header.append("Solo");
    header.append("Instruments");
    header.append("Total Notes");
    header.append("Lowest Note");
    header.append("Highest Note");
    header.append("Occupancy");
    header.append("Concurrency");

    for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
      header.append(Names.getNoteName(semitone));
    }

    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      int noteCount = song.getChannelNoteCount(channel);
      if (noteCount > 0) {
        if (channel != Midi.DRUM) {
          TableRow row = new TableRow();
          int occupancy = song.getOccupancy(channel);
          int concurrency = song.getConcurrency(channel);
          row.append(Value.toNumber(channel));
          row.append(new CheckBox("mute-" + channel));
          row.append(new CheckBox("solo-" + channel));
          row.append(song.getProgramNames(channel));
          row.append(noteCount);
          row.append(Names.formatNote(song.getLowestMidiNote(channel)));
          row.append(Names.formatNote(song.getHighestMidiNote(channel)));
          row.append(occupancy);
          row.append(concurrency);
          for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
            int commonNoteCount = song.getCommonNoteCounts(channel)[semitone];
            row.append(commonNoteCount);
          }
          table.appendChild(row);
        }
      }
    }

    detail.appendChild(table);

    String html = detail.render();
    return html;
  }

}
