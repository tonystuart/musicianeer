// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.message;

import com.example.afs.musicianeer.midi.SongInfoFactory.SongInfo;
import com.example.afs.musicianeer.task.Message;

public class OnSongInfo implements Message {

  private SongInfo songInfo;
  private int songIndex;

  public OnSongInfo(SongInfo songInfo, int songIndex) {
    this.songInfo = songInfo;
    this.songIndex = songIndex;
  }

  public int getSongIndex() {
    return songIndex;
  }

  public SongInfo getSongInfo() {
    return songInfo;
  }

  @Override
  public String toString() {
    return "OnSongInfo [songInfo=" + songInfo + ", songIndex=" + songIndex + "]";
  }

}
