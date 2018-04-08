// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.message.TypedMessage;
import com.example.afs.musicpad.song.Song;

public class OnSong extends TypedMessage {

  private Song song;
  private int index;

  public OnSong(Song song, int index) {
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
    return "OnSong [song=" + song + ", index=" + index + "]";
  }

}