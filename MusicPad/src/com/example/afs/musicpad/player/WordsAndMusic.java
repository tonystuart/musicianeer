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

import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;

public class WordsAndMusic {

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

  private String title;
  private String device;
  private int channel;
  private int lowest;
  private int highest;
  private long duration;
  private int resolution = Default.RESOLUTION;
  private List<BrowserWords> words = new LinkedList<>();
  private List<BrowserMusic> music = new LinkedList<>();

  public WordsAndMusic(Song song, int channel) {
    this.channel = channel;
    this.lowest = Midi.NOTES;
    this.highest = 0;
    this.duration = song.getDuration();
    this.title = song.getName();
    for (Word word : song.getWords()) {
      BrowserWords browserWords = new BrowserWords(word.getTick(), word.getText());
      words.add(browserWords);
    }
    for (Note note : song.getNotes()) {
      if (note.getChannel() == channel) {
        int midiNote = note.getMidiNote();
        long noteTick = note.getTick();
        int noteDuration = (int) note.getDuration();
        BrowserMusic browserMusic = new BrowserMusic(noteTick, midiNote, noteDuration);
        music.add(browserMusic);
        if (midiNote < lowest) {
          lowest = midiNote;
        }
        if (midiNote > highest) {
          highest = midiNote;
        }
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

  @Override
  public String toString() {
    return "WordsAndMusic [title=" + title + ", device=" + device + ", channel=" + channel + ", lowest=" + lowest + ", highest=" + highest + ", duration=" + duration + ", resolution=" + resolution + "]";
  }

}
