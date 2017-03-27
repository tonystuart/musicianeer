// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device;

public class NumericKeypad extends InputDevice {

  public static final char NUM_LOCK = 'N';
  public static final char BACK_SPACE = 'B';

  private static final int PAGE_SIZE = 16;
  private static final int TOTAL_SIZE = PAGE_SIZE * 2;

  @Override
  public int fromIndex(int index) {
    int charCode = -1;
    switch (index) {
    case 0:
      charCode = '1';
      break;
    case 1:
      charCode = '2';
      break;
    case 2:
      charCode = '3';
      break;
    case 3:
      charCode = InputDevice.ENTER;
      break;
    case 4:
      charCode = '4';
      break;
    case 5:
      charCode = '5';
      break;
    case 6:
      charCode = '6';
      break;
    case 7:
      charCode = '+';
      break;
    case 8:
      charCode = '7';
      break;
    case 9:
      charCode = '8';
      break;
    case 10:
      charCode = '9';
      break;
    case 11:
      charCode = '-';
      break;
    case 12:
      charCode = NumericKeypad.NUM_LOCK;
      break;
    case 13:
      charCode = '/';
      break;
    case 14:
      charCode = '*';
      break;
    case 15:
      charCode = NumericKeypad.BACK_SPACE;
      break;
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
    int charCode = -1;
    switch (keyCode) {
    case 69:
      charCode = NumericKeypad.NUM_LOCK;
      break;
    case 98:
      charCode = '/';
      break;
    case 55:
      charCode = '*';
      break;
    case 14:
      charCode = NumericKeypad.BACK_SPACE;
      break;
    case 71:
      charCode = '7';
      break;
    case 72:
      charCode = '8';
      break;
    case 73:
      charCode = '9';
      break;
    case 74:
      charCode = '-';
      break;
    case 75:
      charCode = '4';
      break;
    case 76:
      charCode = '5';
      break;
    case 77:
      charCode = '6';
      break;
    case 78:
      charCode = '+';
      break;
    case 79:
      charCode = '1';
      break;
    case 80:
      charCode = '2';
      break;
    case 81:
      charCode = '3';
      break;
    case 96:
      charCode = InputDevice.ENTER;
      break;
    case 82:
      charCode = '0';
      break;
    case 83:
      charCode = '.';
      break;
    }
    return charCode;
  }

  @Override
  public int toIndex(int charCode) {
    int index = -1;
    switch (charCode) {
    case '1':
      index = 0;
      break;
    case '2':
      index = 1;
      break;
    case '3':
      index = 2;
      break;
    case InputDevice.ENTER:
      index = 3;
      break;
    case '4':
      index = 4;
      break;
    case '5':
      index = 5;
      break;
    case '6':
      index = 6;
      break;
    case '+':
      index = 7;
      break;
    case '7':
      index = 8;
      break;
    case '8':
      index = 9;
      break;
    case '9':
      index = 10;
      break;
    case '-':
      index = 11;
      break;
    case NumericKeypad.NUM_LOCK:
      index = 12;
      break;
    case '/':
      index = 13;
      break;
    case '*':
      index = 14;
      break;
    case NumericKeypad.BACK_SPACE:
      index = 15;
      break;
    }
    return index;
  }

}
