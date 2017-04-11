// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class Return extends Node {

  public Return(int lineNumber, String[] tokens) {
    super(lineNumber);
    if (tokens.length != 1) {
      throw new IllegalArgumentException(formatMessage("Expected return"));
    }
  }

  @Override
  public ReturnState execute(Context context) {
    return ReturnState.RETURN;
  }

}
