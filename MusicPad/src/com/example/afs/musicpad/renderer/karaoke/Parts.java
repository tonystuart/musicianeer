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
import com.example.afs.musicpad.util.FileUtilities;

public class Parts {

  private Song song;
  private int deviceIndex;

  public Parts(Song song, int deviceIndex) {
    this.song = song;
    this.deviceIndex = deviceIndex;
  }

  public String render() {
    Division parts = new Division("#parts", ".tab");
    parts.appendProperty("data-device-index", deviceIndex);
    parts.appendChild(createTitle(deviceIndex));
    parts.appendChild(createContent());
    String html = parts.render();
    return html;
  }

  private Element createContent() {
    Division content = new Division(".content");
    content.appendChild(createLeft());
    content.appendChild(createRight());
    return content;
  }

  private Element createControls() {
    Division controls = new Division(".controls");
    controls.appendChild(createPlayButton());
    controls.appendChild(createStopButton());
    controls.appendChild(createSelectButton());
    return controls;
  }

  private Element createLeft() {
    Division division = new Division(".left");
    division.appendChild(createPartList());
    return division;
  }

  private Element createPartList() {
    Division partList = new Division();
    partList.appendProperty("onclick", "musicPad.selectElement(event.target)");
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      int channelNoteCount = song.getChannelNoteCount(channel);
      if (channelNoteCount != 0) {
        List<String> programNames = song.getProgramNames(channel);
        Division part = new Division("#part-" + channel);
        part.appendProperty("data-channel", channel);
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

  private Element createRight() {
    Division division = new Division(".right");
    division.appendChild(createControls());
    return division;
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
    stopButton.appendProperty("onclick", "karaoke.onStopPart()");
    return stopButton;
  }

  private Element createTitle(int deviceIndex) {
    Division division = new Division(".title");
    division.appendChild(new Division(FileUtilities.getBaseName(song.getTitle())));
    division.appendChild(new Division("Player " + deviceIndex + ": Pick Your Part"));
    return division;
  }

}
