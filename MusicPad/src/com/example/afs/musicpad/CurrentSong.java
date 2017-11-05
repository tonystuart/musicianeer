// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad;

import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.ServiceTask.Response;

public class CurrentSong implements Response {

  private Song song;
  private int index;

  public CurrentSong(Song song, int index) {
    this.song = song;
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public Song getSong() {
    return song;
  }

  @Override
  public String toString() {
    return "CurrentSong [song=" + song + ", index=" + index + "]";
  }
}