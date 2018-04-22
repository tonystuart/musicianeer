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

public class OnPlayCurrentSong implements Message {

  private CurrentSong currentSong;
  private int keyboardTransposition;

  public OnPlayCurrentSong(CurrentSong currentSong, int keyboardTransposition) {
    this.currentSong = currentSong;
    this.keyboardTransposition = keyboardTransposition;
  }

  public CurrentSong getCurrentSong() {
    return currentSong;
  }

  public int getKeyboardTransposition() {
    return keyboardTransposition;
  }

  @Override
  public String toString() {
    return "OnPlayCurrentSong [currentSong=" + currentSong + ", keyboardTransposition=" + keyboardTransposition + "]";
  }

}