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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.renderer.Notator.KeyCap;
import com.example.afs.musicpad.renderer.Notator.Slice;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Device {

  private static final String KEYS = "0.E123456+789-N/*B";

  private Player player;
  private int[] activeMidiNotes = new int[256]; // NB: KeyEvents VK codes, not midiNotes
  private Map<Integer, Integer> keyCapCodeToMidiNote = new HashMap<>();
  private Map<Integer, Character> midiNoteToKeyCap = new HashMap<>();

  public Device(Player player) {
    this.player = player;
    Arrays.fill(activeMidiNotes, -1);
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
    Integer midiNote = keyCapCodeToMidiNote.get(keyCapCode);
    if (midiNote != null) {
      System.out.println("inputCode=" + inputCode + ", keyCapCode=" + keyCapCode + ", midiNote=" + midiNote);
      player.play(Action.PRESS, midiNote);
      activeMidiNotes[inputCode] = midiNote;
    }
  }

  public void onUp(int inputCode) {
    int midiNote = activeMidiNotes[inputCode];
    if (midiNote != -1) {
      player.play(Action.RELEASE, midiNote);
      activeMidiNotes[inputCode] = -1;
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
    Set<Integer> midiNotes = new HashSet<>();
    for (Slice slice : slices) {
      for (Note note : slice) {
        midiNotes.add(note.getMidiNote());
      }
    }
    int index = 0;
    int[] orderedMidiNotes = new int[midiNotes.size()];
    for (int midiNote : midiNotes) {
      orderedMidiNotes[index++] = midiNote;
    }
    Arrays.sort(orderedMidiNotes);
    keyCapCodeToMidiNote.clear();
    midiNoteToKeyCap.clear();
    for (int i = 0; i < orderedMidiNotes.length; i++) {
      keyCapCodeToMidiNote.put(i, orderedMidiNotes[i]);
      midiNoteToKeyCap.put(orderedMidiNotes[i], toKeyCap(i));
    }
    for (Slice slice : slices) {
      StringBuilder s = new StringBuilder();
      for (Note note : slice) {
        if (s.length() > 0) {
          s.append("/");
        }
        int midiNote = note.getMidiNote();
        Character keyCap = midiNoteToKeyCap.get(midiNote);
        s.append(keyCap);
      }
      KeyCap keyCap = new KeyCap(slice, s.toString());
      keyCaps.add(keyCap);
    }
    return keyCaps;
  }

  private char toKeyCap(int noteIndex) {
    char keyCap;
    if (noteIndex < KEYS.length()) {
      keyCap = KEYS.charAt(noteIndex);
    } else {
      keyCap = '?';
    }
    return keyCap;
  }

}
