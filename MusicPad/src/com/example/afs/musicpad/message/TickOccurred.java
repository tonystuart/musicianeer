// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

public class TickOccurred implements Message {

  private long tick;

  public TickOccurred(long tick) {
    this.tick = tick;
  }

  public long getTick() {
    return tick;
  }

  @Override
  public String toString() {
    return "TickOccurred [tick=" + tick + "]";
  }

}
