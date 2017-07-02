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
import java.util.Map.Entry;
import java.util.NavigableMap;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.util.Value;

public class Channels {

  private static final String[] COLORS = new String[] {
      "Red",
      "Green",
      "Blue",
      "Yellow"
  };

  private int deviceIndex;
  private int playerIndex;
  private int defaultChannel;

  private Song song;
  private NavigableMap<Integer, Integer> deviceChannelAssignments;

  public Channels(Song song, int deviceIndex, NavigableMap<Integer, Integer> deviceChannelAssignments) {
    this.song = song;
    this.deviceIndex = deviceIndex;
    this.deviceChannelAssignments = deviceChannelAssignments;
    this.playerIndex = deviceChannelAssignments.size();
    defaultChannel = getDefaultChannel();
  }

  public String render() {
    Division division = new Division("#channels", ".tab", ".player-" + playerIndex);
    division.appendProperty("data-device-index", deviceIndex);
    division.appendProperty("data-player-index", playerIndex);
    division.appendProperty("data-default-channel", defaultChannel);
    division.appendChild(createContent());
    String html = division.render();
    return html;
  }

  private Element createBackToSongsButton() {
    Division division = new Division();
    division.appendChild(new TextElement("Back to Songs"));
    division.appendProperty("onclick", "karaoke.onNewSong()");
    return division;
  }

  private Element createChannelList() {
    Division division = new Division("#channel-list");
    division.appendProperty("onclick", "karaoke.onChannelClick(event.target)");
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      int channelNoteCount = song.getChannelNoteCount(channel);
      if (channelNoteCount != 0) {
        division.appendChild(createChannelListItem(channel));
      }
    }
    return division;
  }

  private Element createChannelListItem(int channel) {
    List<String> programNames = song.getProgramNames(channel);
    Division division = new Division();
    division.appendProperty("data-channel-index", channel);
    division.appendChild(new TextElement(getChannelText(channel, programNames)));
    return division;
  }

  private Element createContent() {
    Division division = new Division(".content");
    division.appendChild(createLeft());
    division.appendChild(createRight());
    return division;
  }

  private Element createControls() {
    Division division = new Division(".controls");
    division.appendChild(createBackToSongsButton());
    division.appendChild(createStopButton());
    division.appendChild(createSelectButton());
    return division;
  }

  private Element createDetails() {
    Division division = new Division(".details");
    return division;
  }

  private Element createLeft() {
    Division division = new Division(".left");
    division.appendChild(createTitle(playerIndex));
    division.appendChild(createChannelList());
    division.appendChild(createControls());
    return division;
  }

  private Element createRight() {
    Division division = new Division(".right");
    division.appendChild(createDetails());
    return division;
  }

  private Element createSelectButton() {
    Division division = new Division();
    division.appendChild(new TextElement("Select this Part"));
    division.appendProperty("onclick", "karaoke.onChannelSelect()");
    return division;
  }

  private Element createStopButton() {
    Division division = new Division();
    division.appendChild(new TextElement("Stop"));
    division.appendProperty("onclick", "karaoke.onStop()");
    return division;
  }

  private Element createTitle(int playerIndex) {
    Division division = new Division(".title");
    String name = getPlayerName(playerIndex);
    division.appendChild(new Division(name + ": Pick your Part"));
    return division;
  }

  private String getChannelText(int channel, List<String> programNames) {
    int index = 0;
    int count = 0;
    StringBuilder s = new StringBuilder();
    s.append("Channel " + Value.toNumber(channel) + ": ");
    s.append(normalize(programNames.get(0)));
    for (Entry<Integer, Integer> entry : deviceChannelAssignments.entrySet()) {
      if (entry.getValue() == channel) {
        if (count++ == 0) {
          s.append(" (");
        } else {
          s.append(", ");
        }
        s.append(getPlayerName(index));
      }
      index++;
    }
    if (count > 0) {
      s.append(")");
    }
    return s.toString();
  }

  private int getDefaultChannel() {
    int firstChannel = -1;
    for (int channel : song.getActiveChannels()) {
      if (isFree(channel)) {
        return channel;
      }
      if (firstChannel == -1) {
        firstChannel = channel;
      }
    }
    return firstChannel;
  }

  private String getPlayerName(int playerIndex) {
    String name;
    if (playerIndex < COLORS.length) {
      name = COLORS[playerIndex] + " Player";
    } else {
      name = "Player " + playerIndex;
    }
    return name;
  }

  private boolean isFree(int channel) {
    for (Entry<Integer, Integer> entry : deviceChannelAssignments.entrySet()) {
      if (entry.getValue() == channel) {
        return false;
      }
    }
    return true;
  }

  private String normalize(String programName) {
    boolean ignore = false;
    boolean capitalize = true;
    StringBuilder s = new StringBuilder();
    int length = programName.length();
    for (int i = 0; i < length; i++) {
      char c = programName.charAt(i);
      if (capitalize) {
        c = Character.toUpperCase(c);
        capitalize = false;
      }
      if (c == '-') {
        c = ' ';
        capitalize = true;
      } else if (c == '(') {
        ignore = true;
      }
      if (!ignore) {
        s.append(c);
      }
      if (c == ')') {
        ignore = false;
      }
    }
    return s.toString();
  }
}
