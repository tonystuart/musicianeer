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
import com.example.afs.musicpad.task.Message;

public class OnSong implements Message {

  private Song song;
  private int index;
  private int keyboardTransposition;

  public OnSong(Song song, int index, int keyboardTransposition) {
    this.song = song;
    this.index = index;
    this.keyboardTransposition = keyboardTransposition;
  }

  public int getIndex() {
    return index;
  }

  public int getKeyboardTransposition() {
    return keyboardTransposition;
  }

  public Song getSong() {
    return song;
  }

  @Override
  public String toString() {
    return "OnSong [song=" + song + ", index=" + index + ", keyboardTransposition=" + keyboardTransposition + "]";
  }

}