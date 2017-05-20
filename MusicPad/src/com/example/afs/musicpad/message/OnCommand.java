// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import java.util.Arrays;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.Trace;

public class OnCommand extends Message {

  private Command command;
  private int[] parameters;

  public OnCommand(Command command, int parameter) {
    this(command, new int[] {
      parameter
    });
  }

  public OnCommand(Command command, int... parameters) {
    this.command = command;
    this.parameters = parameters;
    if (Trace.isTraceCommand()) {
      System.out.println(command + "(" + Arrays.toString(parameters) + ")");
    }
  }

  public Command getCommand() {
    return command;
  }

  public int getParameter() {
    // TODO: Track down all the users of this and modify to support variable parameter lists
    return parameters == null ? 0 : parameters[0];
  }

  public int[] getParameters() {
    return parameters;
  }

  public void setCommand(Command command) {
    this.command = command;
  }

  public void setParameter(int parameter) {
    this.parameters = new int[] {
      parameter
    };
  }

  public void setParameters(int[] parameters) {
    this.parameters = parameters;
  }

  @Override
  public String toString() {
    return "OnCommand [command=" + command + ", parameters=" + Arrays.toString(parameters) + "]";
  }

}
