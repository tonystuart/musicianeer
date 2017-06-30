// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import java.io.File;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.util.RandomAccessList;

public class Songs {

  private RandomAccessList<File> midiFiles;

  public Songs(RandomAccessList<File> midiFiles) {
    this.midiFiles = midiFiles;
  }

  public String render() {
    Division songs = new Division("#songs", ".tab");
    songs.appendChild(createTitle());
    songs.appendChild(createContent());
    String html = songs.render();
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
    division.appendChild(createSongList());
    return division;
  }

  private Element createPlayButton() {
    Division playButton = new Division(".play-button", "Listen to Song");
    playButton.appendProperty("onclick", "karaoke.onPlaySample()");
    return playButton;
  }

  private Element createRight() {
    Division division = new Division(".right");
    division.appendChild(createControls());
    return division;
  }

  private Element createSelectButton() {
    Division selectButton = new Division(".select-button", "Select this Song");
    selectButton.appendProperty("onclick", "karaoke.onSelectSong()");
    return selectButton;
  }

  private Element createSongList() {
    Division songList = new Division(".song-list");
    songList.appendProperty("onclick", "musicPad.selectElement(event.target)");
    int midiFileCount = midiFiles.size();
    for (int i = 0; i < midiFileCount; i++) {
      File midiFile = midiFiles.get(i);
      String name = midiFile.getName();
      int lastDot = name.lastIndexOf('.');
      if (lastDot != -1) {
        name = name.substring(0, lastDot);
      }
      Division song = new Division("#song-" + i);
      song.appendChild(new TextElement(name));
      songList.appendChild(song);
    }
    return songList;
  }

  private Element createStopButton() {
    Division stopButton = new Division(".stop-button", "Stop Listening");
    stopButton.appendProperty("onclick", "karaoke.onStopSample()");
    return stopButton;
  }

  private Element createTitle() {
    Division title = new Division(".title", "Pick Your Song");
    return title;
  }

}
