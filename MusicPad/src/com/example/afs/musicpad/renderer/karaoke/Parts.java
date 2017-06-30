// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import java.util.List;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;

public class Parts {

  private Song song;
  private int[] deviceIndexes;

  public Parts(Song song, int[] deviceIndexes) {
    this.song = song;
    this.deviceIndexes = deviceIndexes;
  }

  public String render() {
    Division songs = new Division("#parts");
    for (int i : deviceIndexes) {
      songs.appendChild(createPart(i));
    }
    String html = songs.render();
    return html;
  }

  private Element createControls() {
    Division controls = new Division();
    controls.appendChild(createPlayButton());
    controls.appendChild(createStopButton());
    controls.appendChild(createSelectButton());
    return controls;
  }

  private Element createHeader(int deviceIndex) {
    return new TextElement("Player " + deviceIndex + ", Pick Your Part");
  }

  private Element createLeft() {
    Division left = new Division();
    left.appendChild(createControls());
    return left;
  }

  private Element createPart(int deviceIndex) {
    Division part = new Division();
    part.setClassName("tab");
    part.appendChild(createHeader(deviceIndex));
    part.appendChild(createLeft());
    part.appendChild(createPartList());
    return part;
  }

  private Element createPartList() {
    Division partList = new Division();
    partList.appendProperty("onclick", "musicPad.selectElement(event.target)");
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      int channelNoteCount = song.getChannelNoteCount(channel);
      if (channelNoteCount != 0) {
        List<String> programNames = song.getProgramNames(channel);
        Division part = new Division("#part-" + channel);
        part.appendChild(new TextElement(programNames.get(0)));
        partList.appendChild(part);
      }
    }
    return partList;
  }

  private Element createPlayButton() {
    Division playButton = new Division();
    playButton.appendChild(new TextElement("Listen to Part"));
    playButton.appendProperty("onclick", "karaoke.onPlayPart()");
    return playButton;
  }

  private Element createSelectButton() {
    Division selectButton = new Division();
    selectButton.appendChild(new TextElement("Select this Part"));
    selectButton.appendProperty("onclick", "karaoke.onSelectPart()");
    return selectButton;
  }

  private Element createStopButton() {
    Division stopButton = new Division();
    stopButton.appendChild(new TextElement("Stop Listening"));
    stopButton.appendProperty("onclick", "karaoke.onStopSample()");
    return stopButton;
  }

}
