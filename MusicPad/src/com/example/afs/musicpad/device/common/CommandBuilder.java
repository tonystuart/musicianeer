// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

import java.awt.event.KeyEvent;
import java.util.concurrent.BlockingQueue;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnNoteOff;
import com.example.afs.musicpad.message.OnNoteOn;
import com.example.afs.musicpad.midi.Midi;

public class CommandBuilder {

  private BlockingQueue<Message> queue;
  private Device device;

  private StringBuilder currentField;
  private StringBuilder left = new StringBuilder();
  private StringBuilder right = new StringBuilder();

  private int octave;
  private boolean sharp;
  private int[] activeMidiNotes = new int[Midi.NOTES];

  public CommandBuilder(BlockingQueue<Message> queue, Device device) {
    this.queue = queue;
    this.device = device;
  }

  public int processKeyDown(int inputCode) {
    int ignoreCount;
    if (currentField == null) {
      if (inputCode == '.') {
        currentField = left;
        ignoreCount = 1;
      } else if (inputCode == KeyEvent.VK_SHIFT || inputCode == KeyEvent.VK_NUM_LOCK) {
        sharp = true;
        ignoreCount = 0;
      } else if (inputCode == '-') {
        octave = -1;
        ignoreCount = 0;
      } else if (inputCode == '+') {
        octave = +1;
        ignoreCount = 0;
      } else if (inputCode == '/') {
        octave = -1;
        sharp = true;
        ignoreCount = 0;
      } else if (inputCode == '*') {
        octave = +1;
        sharp = true;
        ignoreCount = 0;
      } else {
        int midiNote = getMidiNote(inputCode);
        activeMidiNotes[inputCode] = midiNote;
        queue.add(new OnNoteOn(midiNote));
        ignoreCount = 0;
      }
    } else {
      composeField(inputCode);
      ignoreCount = 1;
    }
    return ignoreCount;
  }

  public void processKeyUp(int inputCode) {
    if (currentField == null) {
      if (inputCode == KeyEvent.VK_SHIFT || inputCode == KeyEvent.VK_NUM_LOCK) {
        sharp = false;
      } else if (inputCode == '-') {
        octave = 0;
      } else if (inputCode == '+') {
        octave = 0;
      } else if (inputCode == '/') {
        octave = 0;
        sharp = false;
      } else if (inputCode == '*') {
        octave = 0;
        sharp = false;
      } else {
        int midiNote = activeMidiNotes[inputCode];
        if (midiNote != 0) {
          queue.add(new OnNoteOff(midiNote));
          activeMidiNotes[inputCode] = 0;
        }
      }
    }
  }

  private void clear() {
    left.setLength(0);
    right.setLength(0);
    currentField = null;
  }

  private void composeField(int inputCode) {
    if ('0' <= inputCode && inputCode <= '9') {
      currentField.append((char) inputCode);
      if (currentField == left) {
        System.out.println("left=" + left);
      } else {
        System.out.println("right=" + right);
      }
    } else if (inputCode == KeyEvent.VK_ENTER) {
      if (currentField == left) {
        if (left.length() == 0) {
          left.append("0");
        }
        currentField = right;
      } else {
        if (right.length() == 0) {
          right.append("0");
        }
        createCommand();
        clear();
      }
    } else {
      clear();
    }
  }

  private void createCommand() {
    int index = parseInteger(left.toString());
    int parameter = parseInteger(right.toString());
    Command[] values = Command.values();
    if (index < values.length) {
      Command command = values[index];
      OnCommand onCommand = new OnCommand(command, parameter);
      queue.add(onCommand);
    } else {
      System.err.println("Command " + index + " is out of range.");
    }
  }

  private int getMidiNote(int inputCode) {
    int midiNote = device.getInputMapping().toMidiNote(inputCode);
    midiNote += octave * Midi.SEMITONES_PER_OCTAVE;
    if (sharp) {
      midiNote++;
    }
    return midiNote;
  }

  private int parseInteger(String string) {
    int integer;
    try {
      integer = Integer.parseInt(string);
    } catch (NumberFormatException e) {
      integer = 0;
    }
    return integer;
  }
}