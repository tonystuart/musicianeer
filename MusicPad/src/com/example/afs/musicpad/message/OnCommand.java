// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.Trace;

public class OnCommand extends Message {

  private Command command;
  private int parameter;

  public OnCommand(Command command, int parameter) {
    this.command = command;
    this.parameter = parameter;
    if (Trace.isTraceCommand()) {
      System.out.println(command + "(" + parameter + ")");
    }
  }

  public Command getCommand() {
    return command;
  }

  public int getParameter() {
    return parameter;
  }

  @Override
  public String toString() {
    return "OnCommand [command=" + command + ", parameter=" + parameter + "]";
  }

}
