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
    Division songs = new Division("songs");
    songs.setClassName("tab");
    songs.appendChild(createHeader());
    songs.appendChild(createLeft());
    songs.appendChild(createSongList());
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

  private Element createHeader() {
    return new TextElement("Pick Your Song");
  }

  private Element createLeft() {
    Division left = new Division();
    left.appendChild(createControls());
    return left;
  }

  private Element createPlayButton() {
    Division playButton = new Division();
    playButton.appendChild(new TextElement("Listen to Song"));
    playButton.appendProperty("onclick", "karaoke.onPlaySample()");
    return playButton;
  }

  private Element createSelectButton() {
    Division selectButton = new Division();
    selectButton.appendChild(new TextElement("Select this Song"));
    selectButton.appendProperty("onclick", "karaoke.onSelectSong()");
    return selectButton;
  }

  private Element createSongList() {
    Division songList = new Division();
    songList.appendProperty("onclick", "musicPad.selectElement(event.target, \"song-selector-list\")");
    int midiFileCount = midiFiles.size();
    for (int i = 0; i < midiFileCount; i++) {
      File midiFile = midiFiles.get(i);
      String name = midiFile.getName();
      int lastDot = name.lastIndexOf('.');
      if (lastDot != -1) {
        name = name.substring(0, lastDot);
      }
      Division song = new Division("song-" + i);
      song.appendChild(new TextElement(name));
      songList.appendChild(song);
    }
    return songList;
  }

  private Element createStopButton() {
    Division stopButton = new Division();
    stopButton.appendChild(new TextElement("Stop Listening"));
    stopButton.appendProperty("onclick", "karaoke.onStopSample()");
    return stopButton;
  }

}
