// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import java.util.NavigableMap;

import com.example.afs.musicpad.song.Song;

public class OnSelectChannel extends TypedMessage {

  private Song song;
  private Integer deviceIndex;
  private NavigableMap<Integer, Integer> deviceChannelAssignments;

  public OnSelectChannel(Song song, NavigableMap<Integer, Integer> deviceChannelAssignments, Integer deviceIndex) {
    this.song = song;
    this.deviceChannelAssignments = deviceChannelAssignments;
    this.deviceIndex = deviceIndex;
  }

  public NavigableMap<Integer, Integer> getDeviceChannelAssignments() {
    return deviceChannelAssignments;
  }

  public Integer getDeviceIndex() {
    return deviceIndex;
  }

  public Song getSong() {
    return song;
  }

  @Override
  public String toString() {
    return "OnPickChannel [song=" + song + ", deviceIndex=" + deviceIndex + ", deviceChannelAssignments=" + deviceChannelAssignments + "]";
  }

}
