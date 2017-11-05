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
import com.example.afs.musicpad.html.Node;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.util.Value;

public class ChannelSelector {

  private int deviceIndex;
  private int defaultChannel;

  private Song song;
  private NavigableMap<Integer, Integer> deviceChannelAssignments;

  public ChannelSelector(Song song, int deviceIndex, NavigableMap<Integer, Integer> deviceChannelAssignments) {
    this.song = song;
    this.deviceIndex = deviceIndex;
    this.deviceChannelAssignments = deviceChannelAssignments;
    defaultChannel = getDefaultChannel();
  }

  public String render() {
    Division division = new Division("#channels", ".tab", ".device-" + deviceIndex);
    division.appendProperty("data-device-index", deviceIndex);
    division.appendProperty("data-default-channel", defaultChannel);
    division.appendChild(createContent());
    String html = division.render();
    return html;
  }

  private Node createBackToSongsButton() {
    Division division = new Division();
    division.appendChild(new TextElement("Back to Songs"));
    division.appendProperty("onclick", "karaoke.onBackToSongs()");
    return division;
  }

  private Node createChannelList() {
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

  private Node createChannelListItem(int channel) {
    List<String> programNames = song.getProgramNames(channel);
    Division division = new Division();
    division.appendProperty("data-channel-index", channel);
    division.appendChild(new TextElement(getChannelText(channel, programNames)));
    return division;
  }

  private Node createContent() {
    Division division = new Division(".content");
    division.appendChild(createLeft());
    division.appendChild(createRight());
    return division;
  }

  private Node createControls() {
    Division division = new Division(".controls");
    division.appendChild(createBackToSongsButton());
    division.appendChild(createStopButton());
    division.appendChild(createSelectButton());
    return division;
  }

  private Node createDetails() {
    Division division = new Division("#channel-details", ".details");
    return division;
  }

  private Node createLeft() {
    Division division = new Division(".left");
    division.appendChild(createTitle(deviceIndex));
    division.appendChild(createChannelList());
    division.appendChild(createControls());
    return division;
  }

  private Node createRight() {
    Division division = new Division(".right");
    division.appendChild(createDetails());
    return division;
  }

  private Node createSelectButton() {
    Division division = new Division();
    division.appendChild(new TextElement("Select this Part"));
    division.appendProperty("onclick", "karaoke.onChannelSelect()");
    return division;
  }

  private Node createStopButton() {
    Division division = new Division();
    division.appendChild(new TextElement("Stop"));
    division.appendProperty("onclick", "karaoke.onStop()");
    return division;
  }

  private Node createTitle(int deviceIndex) {
    Division division = new Division(".title");
    String name = Utils.getPlayerName(deviceIndex);
    division.appendChild(new Division(name + ": Pick your Part"));
    return division;
  }

  private String getChannelText(int channel, List<String> programNames) {
    int index = 0;
    int count = 0;
    StringBuilder s = new StringBuilder();
    s.append("Channel " + Value.toNumber(channel) + ": ");
    s.append(programNames.get(0));
    for (Entry<Integer, Integer> entry : deviceChannelAssignments.entrySet()) {
      if (entry.getValue() == channel) {
        if (count++ == 0) {
          s.append(" (");
        } else {
          s.append(", ");
        }
        s.append(Utils.getPlayerName(index));
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

  private boolean isFree(int channel) {
    for (Entry<Integer, Integer> entry : deviceChannelAssignments.entrySet()) {
      if (entry.getValue() == channel) {
        return false;
      }
    }
    return true;
  }
}
