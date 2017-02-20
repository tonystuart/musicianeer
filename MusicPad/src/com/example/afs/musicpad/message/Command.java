// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.util.MessageBroker.Message;

public class Command implements Message {

  private int command;
  private int operand;

  public Command(int command, int operand) {
    this.command = command;
    this.operand = operand;
  }

  public int getCommand() {
    return command;
  }

  public int getOperand() {
    return operand;
  }

  @Override
  public String toString() {
    return "Command [command=" + command + ", operand=" + operand + "]";
  }

}
