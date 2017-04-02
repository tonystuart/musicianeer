// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device;


public class NumericKeypad extends LinuxKeyboard {

  @Override
  public int fromNoteIndex(int noteIndex) {
    int charCode = -1;
    switch (noteIndex) {
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
      charCode = ENTER;
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
      charCode = NUM_LOCK;
      break;
    case 13:
      charCode = '/';
      break;
    case 14:
      charCode = '*';
      break;
    case 15:
      charCode = BACK_SPACE;
      break;
    }
    return charCode;
  }

}
