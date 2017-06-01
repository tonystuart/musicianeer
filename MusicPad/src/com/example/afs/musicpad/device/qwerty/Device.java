// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import com.example.afs.musicpad.player.Chord;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.renderer.Notator.KeyCap;
import com.example.afs.musicpad.renderer.Notator.Slice;
import com.example.afs.musicpad.util.RandomAccessList;

public class Device {

  private Player player;
  private KeyCapMap keyCapMap;
  private Chord[] activeChords = new Chord[256]; // NB: KeyEvents VK codes, not midiNotes

  public Device(Player player) {
    this.player = player;
  }

  public void bendPitch(int pitchBend) {
    player.bendPitch(pitchBend);
  }

  public void changeControl(int control, int value) {
    player.changeControl(control, value);
  }

  public void onDown(int inputCode) {
    Chord chord = keyCapMap.onDown(inputCode);
    if (chord != null) {
      if (chord != null) {
        player.play(Action.PRESS, chord);
        activeChords[inputCode] = chord;
      }
    }
  }

  public void onUp(int inputCode) {
    keyCapMap.onUp(inputCode);
    Chord chord = activeChords[inputCode];
    if (chord != null) {
      player.play(Action.RELEASE, chord);
      activeChords[inputCode] = null;
    }
  }

  public void selectProgram(int program) {
    player.selectProgram(program);
  }

  public void setPercentVelocity(int percentVelocity) {
    player.setPercentVelocity(percentVelocity);
  }

  public RandomAccessList<KeyCap> toKeyCaps(RandomAccessList<Slice> slices) {
    keyCapMap = new KeyCapMap(slices);
    return keyCapMap.getKeyCaps();
  }

}
