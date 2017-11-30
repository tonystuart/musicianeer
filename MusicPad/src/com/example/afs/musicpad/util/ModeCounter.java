// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

public class ModeCounter {

  private int counter;

  public void adjust(boolean isSet) {
    if (isSet) {
      increment();
    } else {
      decrement();
    }
  }

  public void decrement() {
    if (counter == Integer.MIN_VALUE) {
      throw new IllegalStateException("Underflow");
    }
    counter--;
  }

  public void increment() {
    if (counter == Integer.MAX_VALUE) {
      throw new IllegalStateException("Overflow");
    }
    counter++;
  }

  public boolean isSet() {
    return counter > 0;
  }
}
