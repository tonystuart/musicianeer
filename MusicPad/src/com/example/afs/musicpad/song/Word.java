// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

public class Word {

  private long tick;
  private String text;

  public Word(long tick) {
    this.tick = tick;
  }

  public Word(long tick, String text) {
    this.tick = tick;
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public long getTick() {
    return tick;
  }

  @Override
  public String toString() {
    return "Word [tick=" + tick + ", text=" + text + "]";
  }

}
