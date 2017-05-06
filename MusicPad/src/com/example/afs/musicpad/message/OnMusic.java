// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.example.afs.musicpad.device.common.Device;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;

public class OnMusic extends Message {

  public static class Legend {

    private String keyCap;
    private boolean isSharp;

    public Legend() {
    }

    public Legend(String keyCap, boolean isSharp) {
      this.keyCap = keyCap;
      this.isSharp = isSharp;
    }

    public String getKeyCap() {
      return keyCap;
    }

    public boolean isSharp() {
      return isSharp;
    }

    @Override
    public String toString() {
      return "Legend [keyCap=" + keyCap + ", isSharp=" + isSharp + "]";
    }

  }

  public static class Sound {

    private long tick;
    private int sound;
    private int duration;

    public Sound(long tick, int sound, int duration) {
      this.tick = tick;
      this.sound = sound;
      this.duration = duration;
    }

    public int getDuration() {
      return duration;
    }

    public int getSound() {
      return sound;
    }

    public long getTick() {
      return tick;
    }

    @Override
    public String toString() {
      return "Sound [tick=" + tick + ", sound=" + sound + ", duration=" + duration + "]";
    }
  }

  private int index;
  private int channel;
  private int lowest;
  private int highest;
  private long duration;
  private String mappingType;
  private Legend[] legend;
  private int resolution = Default.RESOLUTION;
  private List<Sound> sounds = new LinkedList<>();

  public OnMusic(Song song, Device device, Legend[] legend, int lowest, int highest, List<Sound> sounds) {
    this.duration = song.getDuration();
    this.index = device.getIndex();
    this.channel = device.getChannel();
    this.mappingType = device.getInputMapping().getClass().getSimpleName();
    this.legend = legend;
    this.lowest = lowest;
    this.highest = highest;
    this.sounds = sounds;
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

  public Legend[] getLegend() {
    return legend;
  }

  public int getLowest() {
    return lowest;
  }

  public int getResolution() {
    return resolution;
  }

  public List<Sound> getSounds() {
    return sounds;
  }

  @Override
  public String toString() {
    return "OnChannelkeyCaps [index=" + index + ", channel=" + channel + ", lowest=" + lowest + ", highest=" + highest + ", duration=" + duration + ", mappingType=" + mappingType + ", legend=" + Arrays.toString(legend) + ", resolution=" + resolution + "]";
  }

}
