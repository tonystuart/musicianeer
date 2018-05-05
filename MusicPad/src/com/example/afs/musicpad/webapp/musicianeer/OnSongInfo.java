// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.task.Message;
import com.example.afs.musicpad.webapp.musicianeer.SongInfoFactory.SongInfo;

public class OnSongInfo implements Message {

  private SongInfo songInfo;

  public OnSongInfo(SongInfo songInfo) {
    this.songInfo = songInfo;
  }

  public SongInfo getSongInfo() {
    return songInfo;
  }

  @Override
  public String toString() {
    return "OnSongInfo [songInfo=" + songInfo + "]";
  }

}
