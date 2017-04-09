// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class IfState extends If {

  private ChannelState state;

  public IfState(int lineIndex, String[] tokens) {
    super(lineIndex);
    try {
      state = ChannelState.valueOf(tokens[1]);
    } catch (RuntimeException e) {
      displayError("Expected ifState number");
    }
  }

  @Override
  public String toString() {
    return "IfState [lineNumber=" + getLineNumber() + ", state=" + state + "]";
  }

  @Override
  protected boolean isMatch(Context context) {
    return this.state == context.getChannelState();
  }

}