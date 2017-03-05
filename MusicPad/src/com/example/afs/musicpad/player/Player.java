// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.NavigableSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;

public abstract class Player {

  public enum Action {
    PRESS, RELEASE
  }

  public static final int ITEMS_PER_PAGE = 10;

  protected int page;
  protected Song song;
  protected int channel;
  protected Synthesizer synthesizer;
  private Viewer viewer;

  public Player(Synthesizer synthesizer, Song song, int channel) {
    this.synthesizer = synthesizer;
    this.song = song;
    this.channel = channel;
    this.viewer = new Viewer();
  }

  public void close() {
    viewer.setVisible(false);
  }

  public void displayWordsAndMusic(long currentTick) {
    long ticksPerMeasure = song.getTicksPerMeasure(1);
    long gap = ticksPerMeasure / Default.GAP_BEAT_UNIT;
    long firstTick = currentTick - gap;
    long lastTick = currentTick + (2 * ticksPerMeasure);
    setMusic(getMusic(firstTick, lastTick));
    setWords(getWords(firstTick, lastTick));
  }

  public abstract int getUniqueCount();

  public abstract void play(Action action, int digit);

  public void selectNextPage() {
    int nextPage = page + 1;
    if (nextPage <= getUniqueCount() / ITEMS_PER_PAGE) {
      page = nextPage;
    }
  }

  public void selectPreviousPage() {
    if (page > 0) {
      page--;
    }
  }

  public void selectProgram(int program) {
    synthesizer.changeProgram(channel, program);
  }

  public void setMusic(String music) {
    viewer.getTopLine().setText(music);
  }

  public void setTitle(String title) {
    viewer.setTitle(title);
  }

  public void setWords(String words) {
    viewer.getBottomLine().setText(words);
  }

  protected String getIntroTicks(long currentTick, long firstTick) {
    StringBuilder s = new StringBuilder();
    long deltaTicks = firstTick - currentTick;
    long beats = deltaTicks / Default.TICKS_PER_BEAT;
    for (int i = 0; i < beats; i++) {
      s.append(".");
    }
    return s.toString();
  }

  protected abstract String getMusic(long currentTick, long lastTick);

  protected String getWords(long firstTick, long lastTick) {
    StringBuilder s = new StringBuilder();
    NavigableSet<Word> tickWords = song.getWords().subSet(new Word(firstTick), false, new Word(lastTick), true);
    if (tickWords.size() > 0) {
      Word first = tickWords.first();
      long firstWordTick = first.getTick();
      s.append(getIntroTicks(firstTick, firstWordTick));
      for (Word word : tickWords) {
        String text = word.getText();
        if (text.startsWith("/") || text.startsWith("\\")) {
          text = " " + text.substring(1);
        }
        s.append(text);
      }
    }
    return s.toString();
  }

  protected void playMidiNote(Action action, int midiNote) {
    switch (action) {
    case PRESS:
      synthesizer.pressKey(channel, midiNote, 92);
      break;
    case RELEASE:
      synthesizer.releaseKey(channel, midiNote);
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

}
