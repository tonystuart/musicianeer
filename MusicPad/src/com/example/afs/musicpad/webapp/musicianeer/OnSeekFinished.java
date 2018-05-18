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
import com.example.afs.musicpad.webapp.musicianeer.Transport.Whence;

public class OnSeekFinished implements Message {

  private long tick;
  private Whence whence;

  public OnSeekFinished(long tick, Whence whence) {
    this.tick = tick;
    this.whence = whence;
  }

  public long getTick() {
    return tick;
  }

  public Whence getWhence() {
    return whence;
  }

  @Override
  public String toString() {
    return "OnSeekFinished [tick=" + tick + ", whence=" + whence + "]";
  }

}
