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

public class OnSampleChannel extends TypedMessage {

  private Song song;
  private int deviceIndex;
  private int channel;

  public OnSampleChannel(Song song, int deviceIndex, int channel) {
    this.song = song;
    this.deviceIndex = deviceIndex;
    this.channel = channel;
  }

  public int getChannel() {
    return channel;
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  public Song getSong() {
    return song;
  }

  @Override
  public String toString() {
    return "OnSampleChannel [song=" + song + ", deviceIndex=" + deviceIndex + ", channel=" + channel + "]";
  }

}
