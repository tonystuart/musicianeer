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

import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.renderer.Notator;
import com.example.afs.musicpad.renderer.Notator.KeyCap;
import com.example.afs.musicpad.renderer.Notator.Slice;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class NumericMapping {

  private int octave;
  private int register;
  private boolean sharp;
  private Player player;
  private int[] activeMidiNotes = new int[256]; // NB: KeyEvents VK codes, not midiNotes

  public NumericMapping(Player player) {
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
    int midiNote = -1;
    switch (inputCode) {
    case '/':
      register--;
      break;
    case '*':
      register++;
      break;
    case '-':
      octave--;
      break;
    case '+':
      octave++;
      break;
    case KeyEvent.VK_NUM_LOCK:
      octave = 0;
      register = 0;
      break;
    case '0':
      sharp = true;
      break;
    case '1':
      midiNote = 53;
      break;
    case '2':
      midiNote = 55;
      break;
    case '3':
      midiNote = 57;
      break;
    case '4':
      midiNote = 59;
      break;
    case '5':
      midiNote = 60;
      break;
    case '6':
      midiNote = 62;
      break;
    case '7':
      midiNote = 64;
      break;
    case '8':
      midiNote = 65;
      break;
    case '9':
      midiNote = 67;
      break;
    }
    if (midiNote != -1) {
      midiNote += octave * Midi.SEMITONES_PER_OCTAVE;
      if (register != 0) {
        midiNote += register * Midi.SEMITONES_PER_OCTAVE;
        register = 0;
      }
      if (sharp) {
        midiNote++;
        sharp = false;
      }
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
    int currentOctave = -1;
    for (Slice slice : slices) {
      StringBuilder s = new StringBuilder();
      for (Note note : slice) {
        if (s.length() > 0) {
          s.append(" / ");
        }
        if (currentOctave == -1) {
          s.append("N ");
          currentOctave = 5;
        }
        int midiNote = note.getMidiNote();
        int noteOctave = (midiNote + 5) / Midi.SEMITONES_PER_OCTAVE; // (60 + 5) / 12 -> 5   five because C is on 5 key
        int distance = noteOctave - currentOctave;
        if (distance < 0) {
          if (noteOctave == 5) {
            s.append("N ");
          } else {
            for (int i = 0; i < distance; i++) {
              s.append("- ");
            }
          }
        } else if (distance > 0) {
          if (noteOctave == 5) {
            s.append("N ");
          } else {
            for (int i = 0; i < distance; i++) {
              s.append("+ ");
            }
          }
        }
        s.append(toKeyCap(midiNote));
        currentOctave = noteOctave;
      }
      KeyCap keyCap = new KeyCap(slice, s.toString());
      keyCaps.add(keyCap);
    }
    return keyCaps;
  }

  private String toKeyCap(int midiNote) {
    StringBuilder s = new StringBuilder();
    int scaleNote = midiNote % Midi.SEMITONES_PER_OCTAVE;
    if (Notator.isSharp(scaleNote)) {
      scaleNote--;
      s.append("0 ");
    }
    String noteName = "";
    switch (scaleNote) {
    case 0:
      noteName = "5";
      break;
    case 2:
      noteName = "6";
      break;
    case 4:
      noteName = "7";
      break;
    case 5:
      noteName = "8";
      break;
    case 7:
      noteName = "2";
      break;
    case 9:
      noteName = "3";
      break;
    case 11:
      noteName = "4";
    }
    s.append(noteName);
    return s.toString();
  }

}
