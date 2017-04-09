// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class IfData1 extends If {

  private int data1;

  public IfData1(int lineIndex, String[] tokens) {
    super(lineIndex);
    try {
      data1 = Integer.decode(tokens[1]);
    } catch (RuntimeException e) {
      displayError("Expected ifData1 number");
    }
  }

  @Override
  public String toString() {
    return "IfData1 [lineNumber=" + getLineNumber() + ", data1=" + data1 + "]";
  }

  @Override
  protected boolean isMatch(Context context) {
    return this.data1 == context.getData1();
  }

}