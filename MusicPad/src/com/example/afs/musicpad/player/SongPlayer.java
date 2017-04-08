// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.Set;
import java.util.SortedSet;

import com.example.afs.fluidsynth.Synthesizer;
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
    int ticksPerMeasure = song.getTicksPerMeasure(0);
    int ticksPerGroup = ticksPerMeasure * 2;
    long firstTick = currentTick - (ticksPerMeasure / song.getBeatsPerMeasure(currentTick));
    long lastTick = firstTick + ticksPerGroup;
    int ticksPerCharacter = ticksPerGroup / 16; // line width
    setMusic(getMusic(currentTick, firstTick, lastTick, ticksPerCharacter));
    setWords(getWords(currentTick, firstTick, lastTick, ticksPerCharacter));
  }

  @Deprecated
  public void onTickOld(long currentTick) {
    int ticksPerMeasure = song.getTicksPerMeasure(0);
    int ticksPerGroup = ticksPerMeasure * 2;
    long groupStart = currentTick / ticksPerGroup;
    long firstTick = groupStart * ticksPerGroup;
    long lastTick = firstTick + ticksPerGroup;
    int ticksPerCharacter = ticksPerGroup / 16;
    setMusic(getMusic(currentTick, firstTick, lastTick, ticksPerCharacter));
    setWords(getWords(currentTick, firstTick, lastTick, ticksPerCharacter));
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

  protected abstract String getMusic(long currentTick, long firstTick, long lastTick, int ticksPerCharacter);

  protected String getWords(long currentTick, long firstTick, long lastTick, int ticksPerCharacter) {
    StringBuilder s = new StringBuilder();
    int leftover = 0;
    for (long tick = firstTick; tick < lastTick; tick += ticksPerCharacter) {
      long nextTick = tick + ticksPerCharacter;
      if (currentTick >= tick && currentTick < nextTick) {
        s.append(">");
      } else if (leftover == 0) {
        s.append(" ");
      }

      SortedSet<Word> tickWords = song.getWords().subSet(new Word(tick), new Word(nextTick));
      if (tickWords.size() == 0) {
        if (leftover > 0) {
          leftover -= 2;
        } else {
          s.append(".");
        }
      } else {
        for (Word word : tickWords) {
          //if (currentTick < tick) {
          String text = word.getText();
          if (text.startsWith("/") || text.startsWith("\\")) {
            text = " " + text.substring(1);
          }
          s.append(text);
          leftover = text.length() - 1;
          //  }
        }
      }
    }

    return s.toString();
  }

}
