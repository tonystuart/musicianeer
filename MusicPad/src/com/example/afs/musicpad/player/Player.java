// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.song.Song;

public abstract class Player {

  public enum Action {
    PRESS, RELEASE
  }

  public static final int ITEMS_PER_PAGE = 10;

  protected int page;
  protected Song song;
  protected int channel;
  protected Synthesizer synthesizer;

  public Player(Synthesizer synthesizer, Song song, int channel) {
    this.synthesizer = synthesizer;
    this.song = song;
    this.channel = channel;
  }

  public void displayMusic(long tick) {
    System.out.println("displayMusic: tick=" + tick);
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
