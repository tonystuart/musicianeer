// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device;

import java.awt.event.KeyEvent;

public class AlphabeticKeyboard extends InputDevice {

  private static final int PAGE_SIZE = 26;
  private static final int TOTAL_SIZE = PAGE_SIZE;

  @Override
  public int fromIndex(int index) {
    int charCode = -1;
    if (index >= 0 && index < PAGE_SIZE) {
      charCode = 'A' + index;
    } else if (index >= 27 && index < 37) {
      charCode = '0' + index;
    } else if (index == 37) {
      charCode = '.';
    } else if (index == 38) {
      charCode = ENTER;
    }
    return charCode;
  }

  @Override
  public int getButtonPageSize() {
    return PAGE_SIZE;
  }

  @Override
  public int getButtonTotalSize() {
    return TOTAL_SIZE;
  }

  @Override
  public int toCharCode(int keyCode) {
    int charCode = LinuxKeycodeTable.charCodes[keyCode];
    return charCode;
  }

  @Override
  public int toIndex(int charCode) {
    int index = -1;
    if (charCode >= 'A' && charCode <= 'Z') {
      index = charCode - 'A';
    } else if (charCode >= '0' && charCode <= '9') {
      index = 26 + (charCode - '0');
    } else if (charCode == '.') {
      index = 37;
    } else if (charCode == KeyEvent.VK_ENTER) {
      index = 38;
    }
    return index;
  }
}
