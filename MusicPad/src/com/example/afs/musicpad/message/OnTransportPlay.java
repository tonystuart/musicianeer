// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.main.CurrentSong;
import com.example.afs.musicpad.task.Message;

public class OnTransportPlay implements Message {

  private CurrentSong currentSong;

  public OnTransportPlay(CurrentSong currentSong) {
    this.currentSong = currentSong;
  }

  public CurrentSong getCurrentSong() {
    return currentSong;
  }

  @Override
  public String toString() {
    return "OnPlayCurrentSong [currentSong=" + currentSong + "]";
  }

}