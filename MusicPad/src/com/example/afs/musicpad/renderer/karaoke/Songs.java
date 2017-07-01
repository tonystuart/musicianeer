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
import com.example.afs.musicpad.util.FileUtilities;
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
    controls.appendChild(createRouletteButton());
    controls.appendChild(createSelectButton());
    return controls;
  }

  private Element createLeft() {
    Division division = new Division(".left");
    division.appendChild(createSongList());
    return division;
  }

  private Element createRight() {
    Division division = new Division(".right");
    division.appendChild(createControls());
    return division;
  }

  private Element createRouletteButton() {
    Division selectButton = new Division("Roulette");
    selectButton.appendProperty("onclick", "karaoke.onRoulette()");
    return selectButton;
  }

  private Element createSelectButton() {
    Division selectButton = new Division("Select this Song");
    selectButton.appendProperty("onclick", "karaoke.onSongSelect()");
    return selectButton;
  }

  private Element createSongList() {
    Division songList = new Division("#song-list");
    songList.appendProperty("onclick", "karaoke.onSongClick(event.target)");
    int midiFileCount = midiFiles.size();
    for (int songIndex = 0; songIndex < midiFileCount; songIndex++) {
      File midiFile = midiFiles.get(songIndex);
      String name = FileUtilities.getBaseName(midiFile.getPath());
      Division song = new Division();
      song.appendProperty("data-song-index", songIndex);
      song.appendChild(new TextElement(name));
      songList.appendChild(song);
    }
    return songList;
  }

  private Element createTitle() {
    Division title = new Division(".title", "Pick Your Song");
    return title;
  }

}
