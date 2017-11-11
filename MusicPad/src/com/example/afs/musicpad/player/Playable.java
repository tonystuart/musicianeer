// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

public class Playable {
  private String legend;
  private Sound sound;

  public Playable(Sound sound, String legend) {
    this.sound = sound;
    this.legend = legend;
  }

  public long getBeginTick() {
    return sound.getBeginTick();
  }

  public long getEndTick() {
    return sound.getEndTick();
  }

  public String getLegend() {
    return legend;
  }

  public Sound getSound() {
    return sound;
  }

  @Override
  public String toString() {
    return "Playable [legend=" + legend + ", sound=" + sound + "]";
  }
}