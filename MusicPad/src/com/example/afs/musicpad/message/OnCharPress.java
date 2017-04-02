// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

public class OnCharPress implements Message {

  private int charCode;

  public OnCharPress(int charCode) {
    this.charCode = charCode;
  }

  public int getCharCode() {
    return charCode;
  }

  @Override
  public String toString() {
    return "OnCharPress [charCode=" + charCode + "]";
  }

}
