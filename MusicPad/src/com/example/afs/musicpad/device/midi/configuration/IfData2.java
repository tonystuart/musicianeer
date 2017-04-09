// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class IfData2 extends If {

  private int data2;

  public IfData2(int lineIndex, String[] tokens) {
    super(lineIndex);
    try {
      data2 = Integer.decode(tokens[1]);
    } catch (RuntimeException e) {
      displayError("Expected ifData2 number");
    }
  }

  @Override
  public String toString() {
    return "IfData2 [lineNumber=" + getLineNumber() + ", data2=" + data2 + "]";
  }

  @Override
  protected boolean isMatch(Context context) {
    return this.data2 == context.getData2();
  }

}