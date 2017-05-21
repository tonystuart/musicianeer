// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import java.util.Map;

import com.example.afs.musicpad.song.Song;

public class OnSong extends Message {

  private Song song;
  private Map<Integer, Integer> deviceChannelMap;
  private int ticksPerPixel;

  public OnSong(Song song, Map<Integer, Integer> deviceChannelMap, int ticksPerPixel) {
    this.song = song;
    this.deviceChannelMap = deviceChannelMap;
    this.ticksPerPixel = ticksPerPixel;
  }

  public Map<Integer, Integer> getDeviceChannelMap() {
    return deviceChannelMap;
  }

  public int getTicksPerPixel() {
    return ticksPerPixel;
  }

  public Song getSong() {
    return song;
  }

  @Override
  public String toString() {
    return "OnSong [song=" + song + ", deviceChannelMap=" + deviceChannelMap + ", ticksPerPixel=" + ticksPerPixel + "]";
  }

}
