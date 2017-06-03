// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class ThenSet extends Then {

  public ThenSet(int lineIndex, String[] tokens) {
    super(lineIndex, tokens);
    if (tokens.length < 2 || tokens.length > 3) {
      throw new IllegalArgumentException(formatMessage("Expected set name [value]"));
    }
  }

  @Override
  public void executeThen(Context context) {
    if (tokens.length == 2) {
      context.set(tokens[1], null);
    } else {
      context.set(tokens[1], context.getRight(tokens[2]));
    }
  }

}