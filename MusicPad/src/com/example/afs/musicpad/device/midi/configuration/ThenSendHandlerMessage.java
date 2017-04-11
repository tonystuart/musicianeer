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

public class ThenSendHandlerMessage extends Then {

  public ThenSendHandlerMessage(int lineIndex, String[] tokens) {
    super(lineIndex, tokens);
    if (tokens.length != 2) {
      throw new IllegalArgumentException(formatMessage("Expected sendHandlerMessage data1"));
    }
  }

  @Override
  public void executeThen(Context context) {
    int data1 = (int) context.getRight(tokens[1]);
    context.getHasSendHandlerMessage().sendHandlerMessage(data1);
  }

  @Override
  public String toString() {
    return "ThenSendHandlerMessage [tokens=" + Arrays.toString(tokens) + "]";
  }

}