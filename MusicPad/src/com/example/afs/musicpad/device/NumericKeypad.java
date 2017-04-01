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

public class NumericKeypad extends LinuxKeyboard {

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
      charCode = 'E';
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
      charCode = 'N';
      break;
    case 13:
      charCode = '/';
      break;
    case 14:
      charCode = '*';
      break;
    case 15:
      charCode = 'B';
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
    case 'E':
    case KeyEvent.VK_ENTER:
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
    case 'N':
      index = 12;
      break;
    case '/':
      index = 13;
      break;
    case '*':
      index = 14;
      break;
    case 'B':
    case KeyEvent.VK_BACK_SPACE:
      index = 15;
      break;
    }
    return index;
  }
}
