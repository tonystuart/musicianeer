// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.karaoke;

import com.example.afs.musicpad.playable.Playable;
import com.example.afs.musicpad.util.RandomAccessList;

public class PlayableIterator {

  private int index = 0;
  private RandomAccessList<Playable> playables;

  public PlayableIterator(RandomAccessList<Playable> playables) {
    this.playables = playables;
  }

  public boolean hasNext(long endTick) {
    if (index < playables.size()) {
      return playables.get(index).getBeginTick() < endTick;
    }
    return false;
  }

  public Playable next(long endTick) {
    Playable playable = null;
    if (index < playables.size()) {
      Playable thisPlayable = playables.get(index);
      if (thisPlayable.getBeginTick() < endTick) {
        playable = thisPlayable;
        index++;
      }
    }
    return playable;
  }

}