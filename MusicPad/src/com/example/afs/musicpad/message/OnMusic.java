// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

public class OnMusic extends Message {

  private int index;
  private String music;

  public OnMusic(int index, String music) {
    this.index = index;
    this.music = music;
  }

  public int getIndex() {
    return index;
  }

  public String getMusic() {
    return music;
  }

  @Override
  public String toString() {
    return "OnMusic [index=" + index + ", music=" + music + "]";
  }

}
