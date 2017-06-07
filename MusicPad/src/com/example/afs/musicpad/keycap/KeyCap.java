// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.keycap;

import com.example.afs.musicpad.player.Sound;

public class KeyCap {
  private String legend;
  private Sound sound;

  public KeyCap(Sound sound, String legend) {
    this.sound = sound;
    this.legend = legend;
  }

  public String getLegend() {
    return legend;
  }

  public Sound getSound() {
    return sound;
  }

  public long getTick() {
    return sound.getTick();
  }

  @Override
  public String toString() {
    return "KeyCap [legend=" + legend + ", sound=" + sound + "]";
  }
}