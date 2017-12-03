// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.song.Song;

public class OnSampleSong extends TypedMessage {

  private Song song;
  private int songIndex;

  public OnSampleSong(Song song, int songIndex) {
    this.song = song;
    this.songIndex = songIndex;
  }

  public Song getSong() {
    return song;
  }

  public int getSongIndex() {
    return songIndex;
  }

  @Override
  public String toString() {
    return "OnSampleSong [song=" + song + ", songIndex=" + songIndex + "]";
  }

}
