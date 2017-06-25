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

import com.example.afs.musicpad.html.Button;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.util.RandomAccessList;

public class SongSelector {

  private RandomAccessList<File> midiFiles;

  public SongSelector(RandomAccessList<File> midiFiles) {
    this.midiFiles = midiFiles;
  }

  public String render() {
    Division songSelector = new Division("song-selector");
    songSelector.appendChild(getTitle());
    songSelector.appendChild(getSongList(midiFiles));
    songSelector.appendChild(getControls());
    String html = songSelector.render();
    return html;
  }

  private Element getControls() {
    Division controls = new Division("song-selector-controls");
    controls.appendChild(getPlay());
    controls.appendChild(getStop());
    controls.appendChild(getSelect());
    return controls;
  }

  private Button getPlay() {
    Button button = new Button("song-selector-sample", "Sample");
    button.appendProperty("onclick", "karaoke.onSample()");
    return button;
  }

  private Button getSelect() {
    Button button = new Button("song-selector-select", "Select");
    button.appendProperty("onclick", "karaoke.onSelect()");
    return button;
  }

  private Element getSongList(RandomAccessList<File> midiFiles) {
    Division songList = new Division("song-selector-list");
    songList.appendProperty("onclick", "musicPad.selectElement(event.target, \"song-selector-list\")");
    int midiFileCount = midiFiles.size();
    for (int i = 0; i < midiFileCount; i++) {
      File midiFile = midiFiles.get(i);
      String name = midiFile.getName();
      Division song = new Division("song-" + i);
      song.appendChild(new TextElement(name));
      songList.appendChild(song);
    }
    return songList;
  }

  private Button getStop() {
    Button button = new Button("song-selector-stop", "Stop");
    button.appendProperty("onclick", "karaoke.onSongSelectorStop()");
    return button;
  }

  private Element getTitle() {
    Division title = new Division("song-selector-title");
    title.appendChild(new TextElement("Select a Song"));
    return title;
  }

}
