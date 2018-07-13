// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.message;

import com.example.afs.musicianeer.task.Message;

public class OnTransportProgramChange implements Message {

  private int channel;
  private int program;

  public OnTransportProgramChange(int channel, int program) {
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
    return "OnTransportProgramChange [channel=" + channel + ", program=" + program + "]";
  }

}
