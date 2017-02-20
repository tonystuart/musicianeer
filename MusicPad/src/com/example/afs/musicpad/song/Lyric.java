// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

public class Lyric extends Item<Lyric> {

  private String lyric;

  public Lyric(long tick) {
    super(tick);
  }

  public Lyric(long tick, String lyrics) {
    super(tick);
    this.lyric = lyrics;
  }

  public String getLyric() {
    return lyric;
  }

  @Override
  public int getSortOrder() {
    return SortOrder.LYRIC.ordinal();
  }

  @Override
  public String toString() {
    return "Lyric [tick=" + tick + ", lyric=" + lyric + "]";
  }

}
