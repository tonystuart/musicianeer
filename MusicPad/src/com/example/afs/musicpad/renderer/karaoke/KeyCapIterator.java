// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import com.example.afs.musicpad.keycap.KeyCap;
import com.example.afs.musicpad.util.RandomAccessList;

public class KeyCapIterator {

  private int index = 0;
  private RandomAccessList<KeyCap> keyCaps;

  public KeyCapIterator(RandomAccessList<KeyCap> keyCaps) {
    this.keyCaps = keyCaps;
  }

  public boolean hasNext(long endTick) {
    if (index < keyCaps.size()) {
      return keyCaps.get(index).getBeginTick() < endTick;
    }
    return false;
  }

  public KeyCap next(long endTick) {
    KeyCap keyCap = null;
    if (index < keyCaps.size()) {
      KeyCap thisKeyCap = keyCaps.get(index);
      if (thisKeyCap.getBeginTick() < endTick) {
        keyCap = thisKeyCap;
        index++;
      }
    }
    return keyCap;
  }

}