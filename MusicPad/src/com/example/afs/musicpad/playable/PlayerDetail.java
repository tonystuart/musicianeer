// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.playable;

import com.example.afs.musicpad.task.ServiceTask.Service;
import com.example.afs.musicpad.util.RandomAccessList;

public class PlayerDetail {

  public static class PlayerDetailService implements Service<PlayerDetail> {
    private int deviceIndex;

    public PlayerDetailService(int deviceIndex) {
      this.deviceIndex = deviceIndex;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      PlayerDetailService other = (PlayerDetailService) obj;
      if (deviceIndex != other.deviceIndex) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + deviceIndex;
      return result;
    }
  }

  public static String getPlayableDeviceKey(int deviceIndex) {
    return "playable-device-" + deviceIndex;
  }

  private RandomAccessList<Playable> playables;
  private int channelIndex;
  private int programIndex;

  public PlayerDetail(RandomAccessList<Playable> playables, int channelIndex, int programIndex) {
    this.playables = playables;
    this.channelIndex = channelIndex;
    this.programIndex = programIndex;
  }

  public int getChannelIndex() {
    return channelIndex;
  }

  public RandomAccessList<Playable> getPlayables() {
    return playables;
  }

  public int getProgramIndex() {
    return programIndex;
  }

  @Override
  public String toString() {
    return "PlayerDetail [channelIndex=" + channelIndex + ", programIndex=" + programIndex + ", playables=" + playables + "]";
  }
}
