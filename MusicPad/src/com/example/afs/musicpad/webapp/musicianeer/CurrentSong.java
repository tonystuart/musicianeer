// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.song.Song;

public class CurrentSong {
  private int index;
  private Song song;

  public CurrentSong(int index, Song song) {
    this.index = index;
    this.song = song;
  }

  public int getIndex() {
    return index;
  }

  public Song getSong() {
    return song;
  }

  @Override
  public String toString() {
    return "CurrentSong [index=" + index + ", song=" + song + "]";
  }

}