// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.LinkedList;
import java.util.List;

import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;

public class Prompter {

  public static class BrowserMusic {

    private long tick;
    private int note;
    private int duration;

    public BrowserMusic() {
    }

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

    public void setDuration(int duration) {
      this.duration = duration;
    }

    public void setNote(int note) {
      this.note = note;
    }

    public void setTick(long tick) {
      this.tick = tick;
    }

    @Override
    public String toString() {
      return "BrowserMusic [tick=" + tick + ", note=" + note + ", duration=" + duration + "]";
    }
  }

  public static class BrowserWords {

    private long tick;
    private String text;

    public BrowserWords() {
    }

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

    public void setText(String words) {
      this.text = words;
    }

    public void setTick(long tick) {
      this.tick = tick;
    }

    @Override
    public String toString() {
      return "BrowserWords [tick=" + tick + ", text=" + text + "]";
    }
  }

  private String title;
  private String device;
  private int channel;
  private int lowest;
  private int highest;
  private int resolution = Default.TICKS_PER_BEAT / 2;
  private long duration;
  private List<BrowserWords> words = new LinkedList<>();
  private List<BrowserMusic> music = new LinkedList<>();

  public Prompter(Song song, int channel) {
    this.channel = channel;
    this.duration = song.getNotes().last().getTick();
    this.highest = getHighestMidiNote(song);
    this.lowest = getLowestMidiNote(song);
    this.title = song.getName();
    for (Word word : song.getWords()) {
      BrowserWords browserWords = new BrowserWords(word.getTick(), word.getText());
      words.add(browserWords);
    }
    for (Note note : song.getNotes()) {
      if (note.getChannel() == channel) {
        BrowserMusic browserMusic = new BrowserMusic(note.getTick(), note.getMidiNote(), (int) note.getDuration());
        music.add(browserMusic);
      }
    }
  }

  public int getChannel() {
    return channel;
  }

  public String getDevice() {
    return device;
  }

  public long getDuration() {
    return duration;
  }

  public int getHighest() {
    return highest;
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

  public String getTitle() {
    return title;
  }

  public List<BrowserWords> getWords() {
    return words;
  }

  public void setChannel(int channel) {
    this.channel = channel;
  }

  public void setDevice(String device) {
    this.device = device;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public void setHighest(int highest) {
    this.highest = highest;
  }

  public void setLowest(int lowest) {
    this.lowest = lowest;
  }

  public void setMusic(List<BrowserMusic> browserMusic) {
    this.music = browserMusic;
  }

  public void setResolution(int resolution) {
    this.resolution = resolution;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setWords(List<BrowserWords> browserWords) {
    this.words = browserWords;
  }

  private int getHighestMidiNote(Song song) {
    int[] noteCounts = song.getDistinctNoteCount(channel);
    for (int midiNote = noteCounts.length - 1; midiNote >= 0; midiNote--) {
      if (noteCounts[midiNote] != 0) {
        return midiNote;
      }
    }
    return -1;
  }

  private int getLowestMidiNote(Song song) {
    int[] noteCounts = song.getDistinctNoteCount(channel);
    for (int midiNote = 0; midiNote < noteCounts.length; midiNote++) {
      if (noteCounts[midiNote] != 0) {
        return midiNote;
      }
    }
    return -1;
  }

}
