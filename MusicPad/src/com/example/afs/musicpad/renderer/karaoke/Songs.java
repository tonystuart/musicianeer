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
    Division division = new Division("#songs", ".content", ".tab");
    division.appendChild(createLeft());
    division.appendChild(createRight());
    String html = division.render();
    return html;
  }

  private Element createControls() {
    Division division = new Division(".controls");
    division.appendChild(createRouletteButton());
    division.appendChild(createStopButton());
    division.appendChild(createSelectButton());
    return division;
  }

  private Element createDetails() {
    Element division = new Division(".details");
    return division;
  }

  private Element createLeft() {
    Division division = new Division(".left");
    division.appendChild(createTitle());
    division.appendChild(createSongList());
    division.appendChild(createControls());
    return division;
  }

  private Element createRight() {
    Division division = new Division(".right");
    division.appendChild(createDetails());
    return division;
  }

  private Element createRouletteButton() {
    Division division = new Division("Roulette");
    division.appendProperty("onclick", "karaoke.onSongRoulette()");
    return division;
  }

  private Element createSelectButton() {
    Division division = new Division("Select this Song");
    division.appendProperty("onclick", "karaoke.onSongSelect()");
    return division;
  }

  private Element createSongList() {
    Division division = new Division("#song-list");
    division.appendProperty("onclick", "karaoke.onSongClick(event.target)");
    int midiFileCount = midiFiles.size();
    for (int songIndex = 0; songIndex < midiFileCount; songIndex++) {
      File midiFile = midiFiles.get(songIndex);
      String name = FileUtilities.getBaseName(midiFile.getPath());
      Division song = new Division();
      song.appendProperty("data-song-index", songIndex);
      song.appendChild(new TextElement(name));
      division.appendChild(song);
    }
    return division;
  }

  private Element createStopButton() {
    Division division = new Division();
    division.appendChild(new TextElement("Stop"));
    division.appendProperty("onclick", "karaoke.onStop()");
    return division;
  }

  private Element createTitle() {
    Division division = new Division(".title", "Pick a Song");
    return division;
  }

}
