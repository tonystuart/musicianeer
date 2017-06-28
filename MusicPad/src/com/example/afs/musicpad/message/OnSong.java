// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import java.util.Arrays;

import com.example.afs.musicpad.song.Song;

public class OnSong extends Message {

  private Song song;
  private int ticksPerPixel;
  private int[] deviceIndexes;

  public OnSong(Song song, int[] deviceIndexes, int ticksPerPixel) {
    this.song = song;
    this.deviceIndexes = deviceIndexes;
    this.ticksPerPixel = ticksPerPixel;
  }

  public int[] getDeviceIndexes() {
    return deviceIndexes;
  }

  public Song getSong() {
    return song;
  }

  public int getTicksPerPixel() {
    return ticksPerPixel;
  }

  @Override
  public String toString() {
    return "OnSong [song=" + song + ", deviceIndexes=" + Arrays.toString(deviceIndexes) + ", ticksPerPixel=" + ticksPerPixel + "]";
  }

}
