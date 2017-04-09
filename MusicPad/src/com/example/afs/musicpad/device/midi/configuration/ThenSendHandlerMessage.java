// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class ThenSendHandlerMessage extends Then {

  private int data1;

  public ThenSendHandlerMessage(int lineIndex, String[] tokens) {
    super(lineIndex);
    try {
      data1 = Integer.decode(tokens[1]);
    } catch (RuntimeException e) {
      displayError("Expected sendHandlerMessage data1");
    }
  }

  @Override
  public void executeThen(Context context) {
    context.getConfigurationSupport().sendHandlerMessage(data1);
  }

  @Override
  public String toString() {
    return "SendHandlerMessage [lineNumber=" + getLineNumber() + ", data1=" + data1 + "]";
  }

}