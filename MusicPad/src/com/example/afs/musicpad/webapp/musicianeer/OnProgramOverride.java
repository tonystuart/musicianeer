// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.task.Message;

public class OnProgramOverride implements Message {

  public static final int DEFAULT = -1;

  private int channel;
  private int program;

  public OnProgramOverride(int channel, int program) {
    this.channel = channel;
    this.program = program;
  }

  public int getChannel() {
    return channel;
  }

  public int getProgram() {
    return program;
  }

  @Override
  public String toString() {
    return "OnProgramOverride [channel=" + channel + ", program=" + program + "]";
  }

}
