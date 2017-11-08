// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.playable;

import com.example.afs.musicpad.task.ServiceTask.Response;
import com.example.afs.musicpad.util.RandomAccessList;

public class Playables implements Response {

  public static String getPlayableDeviceKey(int deviceIndex) {
    return "playables-" + deviceIndex;
  }

  private RandomAccessList<Playable> playables;

  public Playables(RandomAccessList<Playable> playables) {
    this.playables = playables;
  }

  public RandomAccessList<Playable> getPlayables() {
    return playables;
  }
}
