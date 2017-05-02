// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.example.afs.musicpad.device.common.Device;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;

public class PrompterData {

  public static class BrowserMusic {

    private long tick;
    private int note;
    private int duration;

    public BrowserMusic(long tick, int note, int duration) {
      this.tick = tick;
      this.note = note;
      this.duration = duration;
    }

    public int getDuration() {
      return duration;
    }

    public int getNote() {
      return note;
    }

    public long getTick() {
      return tick;
    }

    @Override
    public String toString() {
      return "BrowserMusic [tick=" + tick + ", note=" + note + ", duration=" + duration + "]";
    }
  }

  public static class BrowserWords {

    private long tick;
    private String text;

    public BrowserWords(long tick, String words) {
      this.tick = tick;
      this.text = words;
    }

    public String getText() {
      return text;
    }

    public long getTick() {
      return tick;
    }

    @Override
    public String toString() {
      return "BrowserWords [tick=" + tick + ", text=" + text + "]";
    }
  }

  private int index;
  private int channel;
  private int lowest;
  private int highest;
  private long duration;
  private String type;
  private String[] legend;
  private int resolution = Default.RESOLUTION;
  private List<BrowserWords> words = new LinkedList<>();
  private List<BrowserMusic> music = new LinkedList<>();

  public PrompterData(Song song, Device device, String[] legend, int lowest, int highest, List<BrowserWords> words, List<BrowserMusic> music) {
    this.duration = song.getDuration();
    this.index = device.getIndex();
    this.channel = device.getChannel();
    this.type = device.getInputMapping().getClass().getSimpleName();
    this.legend = legend;
    this.lowest = lowest;
    this.highest = highest;
    this.words = words;
    this.music = music;
  }

  public int getChannel() {
    return channel;
  }

  public long getDuration() {
    return duration;
  }

  public int getHighest() {
    return highest;
  }

  public int getIndex() {
    return index;
  }

  public String[] getLegend() {
    return legend;
  }

  public int getLowest() {
    return lowest;
  }

  public List<BrowserMusic> getMusic() {
    return music;
  }

  public int getResolution() {
    return resolution;
  }

  public List<BrowserWords> getWords() {
    return words;
  }

  @Override
  public String toString() {
    return "PrompterData [index=" + index + ", channel=" + channel + ", lowest=" + lowest + ", highest=" + highest + ", duration=" + duration + ", type=" + type + ", legend=" + Arrays.toString(legend) + ", resolution=" + resolution + "]";
  }

}
