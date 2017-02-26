// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

public abstract class Item<T extends Item<T>> implements Comparable<T> {

  protected long tick;

  protected Item(long tick) {
    this.tick = tick;
  }

  @Override
  public int compareTo(T that) {
    int deltaTick = (int) (this.tick - that.tick);
    if (deltaTick != 0) {
      return deltaTick;
    }
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Item<?> other = (Item<?>) obj;
    if (tick != other.tick) {
      return false;
    }
    return true;
  }

  public long getTick() {
    return tick;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (tick ^ (tick >>> 32));
    return result;
  }

}
