// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

import java.util.Arrays;

public class ThenClear extends Then {

  public ThenClear(int lineIndex, String[] tokens) {
    super(lineIndex, tokens);
    if (tokens.length != 2) {
      throw new IllegalArgumentException(formatMessage("Expected clear name"));
    }
  }

  @Override
  public void executeThen(Context context) {
    context.remove(tokens[1]);
  }

  @Override
  public String toString() {
    return "ThenSendDeviceMessage [tokens=" + Arrays.toString(tokens) + "]";
  }

}