// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.frankenmusic.loader;

public class Name {

  private int song;
  private String name;

  public Name(int song, String name) {
    this.song = song;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public int getSong() {
    return song;
  }

  @Override
  public String toString() {
    return "SongName [song=" + song + ", name=" + name + "]";
  }

}