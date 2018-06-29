// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.main;

import com.example.afs.musicianeer.midi.SongInfoFactory.SongInfo;
import com.example.afs.musicianeer.song.Song;

public class CurrentSong {

  private Song song;
  private SongInfo songInfo;
  private int songIndex;

  public CurrentSong(Song song, SongInfo songInfo, int songIndex) {
    this.song = song;
    this.songInfo = songInfo;
    this.songIndex = songIndex;
  }

  public Song getSong() {
    return song;
  }

  public int getSongIndex() {
    return songIndex;
  }

  public SongInfo getSongInfo() {
    return songInfo;
  }

  @Override
  public String toString() {
    return "CurrentSong [song=" + song + ", songInfo=" + songInfo + ", songIndex=" + songIndex + "]";
  }

}