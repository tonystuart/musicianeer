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
import java.util.Set;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;

public abstract class SongPlayer extends Player {

  protected Song song;
  private Viewer viewer;

  public SongPlayer(Synthesizer synthesizer, Song song, int channel) {
    super(synthesizer, channel);
    this.song = song;
    this.viewer = new Viewer();
    Set<Integer> programs = song.getPrograms(channel);
    if (programs.size() > 0) {
      int program = programs.iterator().next();
      selectProgram(program);
    }
  }

  @Override
  public void close() {
    viewer.setVisible(false);
  }

  @Override
  public void onTick(long currentTick) {
    long ticksPerMeasure = song.getTicksPerMeasure(1);
    long gap = ticksPerMeasure / Default.GAP_BEAT_UNIT;
    long firstTick = currentTick - gap;
    long lastTick = currentTick + (2 * ticksPerMeasure);
    setMusic(getMusic(firstTick, lastTick));
    setWords(getWords(firstTick, lastTick));
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

}
