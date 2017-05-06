// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import java.util.LinkedList;
import java.util.List;

import com.example.afs.musicpad.song.Default;

public class OnWords extends Message {

  public static class Lyric {

    private long tick;
    private String lyric;

    public Lyric() {
    }

    public Lyric(long tick, String lyric) {
      this.tick = tick;
      this.lyric = lyric;
    }

    public String getLyric() {
      return lyric;
    }

    public long getTick() {
      return tick;
    }

    @Override
    public String toString() {
      return "SongWord [tick=" + tick + ", lyric=" + lyric + "]";
    }
  }

  private long duration;
  private int resolution = Default.RESOLUTION;
  private List<Lyric> lyrics = new LinkedList<>();

  public OnWords() {
  }

  public OnWords(List<Lyric> lyrics, long duration) {
    this.lyrics = lyrics;
    this.duration = duration;
  }

  public long getDuration() {
    return duration;
  }

  public List<Lyric> getLyrics() {
    return lyrics;
  }

  public int getResolution() {
    return resolution;
  }

}