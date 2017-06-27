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

public class SongSelector {

  private RandomAccessList<File> midiFiles;

  public SongSelector(RandomAccessList<File> midiFiles) {
    this.midiFiles = midiFiles;
  }

  public String render() {
    Element songList = getSongList(midiFiles);
    String html = songList.render();
    return html;
  }

  private Element getSongList(RandomAccessList<File> midiFiles) {
    Division songList = new Division("song-selector-list");
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

}
