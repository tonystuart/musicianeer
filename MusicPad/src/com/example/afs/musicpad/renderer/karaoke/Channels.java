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

public class Channels {

  private Song song;
  private int deviceIndex;

  public Channels(Song song, int deviceIndex) {
    this.song = song;
    this.deviceIndex = deviceIndex;
  }

  public String render() {
    Division channels = new Division("#channels", ".tab");
    channels.appendProperty("data-device-index", deviceIndex);
    channels.appendChild(createTitle(deviceIndex));
    channels.appendChild(createContent());
    String html = channels.render();
    return html;
  }

  private Element createBackToSongsButton() {
    Division playButton = new Division();
    playButton.appendChild(new TextElement("Back to Songs"));
    playButton.appendProperty("onclick", "karaoke.newSong()");
    return playButton;
  }

  private Element createChannelList() {
    Division channelList = new Division("#channel-list");
    channelList.appendProperty("onclick", "karaoke.onChannelClick(event.target)");
    for (int channelIndex = 0; channelIndex < Midi.CHANNELS; channelIndex++) {
      int channelNoteCount = song.getChannelNoteCount(channelIndex);
      if (channelNoteCount != 0) {
        List<String> programNames = song.getProgramNames(channelIndex);
        Division channel = new Division();
        channel.appendProperty("data-channel-index", channelIndex);
        channel.appendChild(new TextElement(programNames.get(0)));
        channelList.appendChild(channel);
      }
    }
    return channelList;
  }

  private Element createContent() {
    Division content = new Division(".content");
    content.appendChild(createLeft());
    content.appendChild(createRight());
    return content;
  }

  private Element createControls() {
    Division controls = new Division(".controls");
    controls.appendChild(createBackToSongsButton());
    controls.appendChild(createSelectButton());
    return controls;
  }

  private Element createLeft() {
    Division division = new Division(".left");
    division.appendChild(createChannelList());
    return division;
  }

  private Element createRight() {
    Division division = new Division(".right");
    division.appendChild(createControls());
    return division;
  }

  private Element createSelectButton() {
    Division selectButton = new Division();
    selectButton.appendChild(new TextElement("Select this Channel"));
    selectButton.appendProperty("onclick", "karaoke.onChannelSelect()");
    return selectButton;
  }

  private Element createTitle(int deviceIndex) {
    Division division = new Division(".title");
    division.appendChild(new Division("Player " + deviceIndex + ": Pick Your Channel"));
    division.appendChild(new Division(FileUtilities.getBaseName(song.getTitle())));
    return division;
  }

}
