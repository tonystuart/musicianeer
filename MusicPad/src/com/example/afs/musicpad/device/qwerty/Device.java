// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.example.afs.musicpad.player.Chord;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.renderer.Notator.KeyCap;
import com.example.afs.musicpad.renderer.Notator.Slice;
import com.example.afs.musicpad.util.Count;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Device {

  private static final String KEYS = "0.E123456+789-N/*B";

  private Player player;
  private Chord[] activeChords = new Chord[256]; // NB: KeyEvents VK codes, not midiNotes
  private Map<Integer, Chord> keyCapCodeToChord = new HashMap<>();
  private Map<Chord, String> chordToKeyCap = new HashMap<>();

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
    int keyCapCode = -1;
    switch (inputCode) {
    case '0':
      keyCapCode = 0;
      break;
    case '.':
      keyCapCode = 1;
      break;
    case KeyEvent.VK_ENTER:
      keyCapCode = 2;
      break;
    case '1':
      keyCapCode = 3;
      break;
    case '2':
      keyCapCode = 4;
      break;
    case '3':
      keyCapCode = 5;
      break;
    case '4':
      keyCapCode = 6;
      break;
    case '5':
      keyCapCode = 7;
      break;
    case '6':
      keyCapCode = 8;
      break;
    case '+':
      keyCapCode = 9;
      break;
    case '7':
      keyCapCode = 10;
      break;
    case '8':
      keyCapCode = 11;
      break;
    case '9':
      keyCapCode = 12;
      break;
    case '-':
      keyCapCode = 13;
      break;
    case KeyEvent.VK_NUM_LOCK:
      keyCapCode = 14;
      break;
    case '/':
      keyCapCode = 15;
      break;
    case '*':
      keyCapCode = 16;
      break;
    case KeyEvent.VK_BACK_SPACE:
      keyCapCode = 17;
      break;
    }
    Chord chord = keyCapCodeToChord.get(keyCapCode);
    if (chord != null) {
      System.out.println("inputCode=" + inputCode + ", keyCapCode=" + keyCapCode + ", chord=" + chord);
      player.play(Action.PRESS, chord);
      activeChords[inputCode] = chord;
    }
  }

  public void onUp(int inputCode) {
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
    RandomAccessList<KeyCap> keyCaps = new DirectList<>();
    Map<Chord, Count<Chord>> chords = new HashMap<>();
    for (Slice slice : slices) {
      Chord chord = slice.getChord();
      Count<Chord> count = chords.get(chord);
      if (count == null) {
        count = new Count<Chord>(chord);
        chords.put(chord, count);
      }
      count.increment();
    }
    DirectList<Count<Chord>> orderedChords = new DirectList<>(chords.values());
    orderedChords.sort((e1, e2) -> -e1.compareTo(e2));
    while (orderedChords.size() > KEYS.length()) {
      orderedChords.remove(orderedChords.size() - 1);
    }
    int chordCount = orderedChords.size();
    orderedChords.sort((e1, e2) -> e1.getValue().compareTo(e2.getValue()));
    keyCapCodeToChord.clear();
    chordToKeyCap.clear();
    for (int i = 0; i < chordCount; i++) {
      Chord chord = orderedChords.get(i).getValue();
      keyCapCodeToChord.put(i, chord);
      chordToKeyCap.put(chord, toKeyCap(i));
    }
    for (Slice slice : slices) {
      Chord chord = slice.getChord();
      KeyCap keyCap = new KeyCap(slice, chordToKeyCap.get(chord));
      keyCaps.add(keyCap);
    }
    return keyCaps;
  }

  private String toKeyCap(int noteIndex) {
    String keyCap;
    if (noteIndex < KEYS.length()) {
      keyCap = String.valueOf(KEYS.charAt(noteIndex));
    } else {
      keyCap = "?";
    }
    return keyCap;
  }

}
