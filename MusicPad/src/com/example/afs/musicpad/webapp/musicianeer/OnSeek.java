// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.task.Message;

public class OnSeek implements Message {

  private long tick;

  public OnSeek(long tick) {
    this.tick = tick;
  }

  public long getTick() {
    return tick;
  }

  @Override
  public String toString() {
    return "OnSeek [tick=" + tick + "]";
  }

}
