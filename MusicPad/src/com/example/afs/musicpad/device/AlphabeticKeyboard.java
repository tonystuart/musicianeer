// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device;

public class AlphabeticKeyboard extends LinuxKeyboard {

  @Override
  public int fromIndex(int index) {
    int charCode = -1;
    if (index >= 0 && index < 26) {
      charCode = 'A' + index;
    }
    return charCode;
  }

  @Override
  public int toIndex(int charCode) {
    int index = -1;
    if (charCode >= 'A' && charCode <= 'Z') {
      index = charCode - 'A';
    }
    return index;
  }
}
