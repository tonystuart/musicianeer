// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

import java.awt.event.KeyEvent;
import java.util.Arrays;

public class InputMap {
  private int[] inputCodes;
  private String[] legends;

  public InputMap(int[] inputCodes, String[] legends) {
    this.inputCodes = inputCodes;
    this.legends = legends;
  }

  public InputMap(String s) {
    int length = s.length();
    inputCodes = new int[length];
    legends = new String[length];
    for (int index = 0; index < length; index++) {
      char c = s.charAt(index);
      inputCodes[index] = c;
      legends[index] = KeyEvent.getKeyText(c);
    }
  }

  public int[] getInputCodes() {
    return inputCodes;
  }

  public String[] getLegends() {
    return legends;
  }

  @Override
  public String toString() {
    return "InputMap [inputCodes=" + Arrays.toString(inputCodes) + ", legends=" + Arrays.toString(legends) + "]";
  }

}