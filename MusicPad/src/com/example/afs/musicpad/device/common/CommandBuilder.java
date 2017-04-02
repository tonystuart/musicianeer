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
import com.example.afs.musicpad.message.OnInputRelease;
import com.example.afs.musicpad.message.OnInputPress;

public class CommandBuilder {

  private BlockingQueue<Message> queue;

  private StringBuilder left = new StringBuilder();
  private StringBuilder right = new StringBuilder();
  private StringBuilder currentField;

  public CommandBuilder(BlockingQueue<Message> queue) {
    this.queue = queue;
  }

  public void processInputPress(int inputCode) {
    if (currentField == null) {
      if (inputCode == '.') {
        currentField = left;
      } else if (inputCode != -1) {
        queue.add(new OnInputPress(inputCode));
      }
    } else {
      composeField(inputCode);
    }
  }

  public void processInputRelease(int inputCode) {
    if (currentField == null) {
      if (inputCode != -1) {
        queue.add(new OnInputRelease(inputCode));
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
      System.out.println("Processing " + command + "(" + parameter + ")");
      OnCommand onCommand = new OnCommand(command, parameter);
      queue.add(onCommand);
    } else {
      System.err.println("Command " + index + " is out of range.");
    }
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