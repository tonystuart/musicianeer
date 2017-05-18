// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import java.util.Arrays;

public class OnSongList extends Message {

  private String[] songList;

  public OnSongList(String[] songList) {
    this.songList = songList;
  }

  public String[] getSongList() {
    return songList;
  }

  @Override
  public String toString() {
    return "OnSongList [songList=" + Arrays.toString(songList) + "]";
  }

}
