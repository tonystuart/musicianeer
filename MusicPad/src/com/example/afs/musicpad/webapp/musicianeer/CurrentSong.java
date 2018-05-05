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
import com.example.afs.musicpad.webapp.musicianeer.SongInfoFactory.SongInfo;

public class CurrentSong {

  private Song song;
  private SongInfo songInfo;

  public CurrentSong(Song song, SongInfo songInfo) {
    this.song = song;
    this.songInfo = songInfo;
  }

  public Song getSong() {
    return song;
  }

  public SongInfo getSongInfo() {
    return songInfo;
  }

  @Override
  public String toString() {
    return "CurrentSong [song=" + song + ", songInfo=" + songInfo + "]";
  }

}