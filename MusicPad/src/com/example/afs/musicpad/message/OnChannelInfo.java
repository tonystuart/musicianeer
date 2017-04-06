// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.song.Song;

public class OnChannelInfo implements Message {

  private Song song;
  private int channel;
  private int noteCount;
  private int occupancy;
  private int concurrency;

  public OnChannelInfo(Song song, int channel, int noteCount, int occupancy, int concurrency) {
    this.song = song;
    this.channel = channel;
    this.noteCount = noteCount;
    this.occupancy = occupancy;
    this.concurrency = concurrency;
  }

  public int getChannel() {
    return channel;
  }

  public int getNoteCount() {
    return noteCount;
  }

  public int getConcurrency() {
    return concurrency;
  }

  public int getOccupancy() {
    return occupancy;
  }

  public Song getSong() {
    return song;
  }

  @Override
  public String toString() {
    return "OnChannelInfo [song=" + song + ", channel=" + channel + ", channelNoteCount=" + noteCount + ", occupancy=" + occupancy + ", concurrency=" + concurrency + "]";
  }

}
