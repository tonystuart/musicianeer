// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;



public class NumericMapping extends QwertyMapping {

  @Override
  public int toInputCode(int noteIndex) {
    int inputCode = NO_CODE_FOR_NOTE;
    switch (noteIndex) {
    case 0:
      inputCode = '1';
      break;
    case 1:
      inputCode = '2';
      break;
    case 2:
      inputCode = '3';
      break;
    case 3:
      inputCode = ENTER;
      break;
    case 4:
      inputCode = '4';
      break;
    case 5:
      inputCode = '5';
      break;
    case 6:
      inputCode = '6';
      break;
    case 7:
      inputCode = '+';
      break;
    case 8:
      inputCode = '7';
      break;
    case 9:
      inputCode = '8';
      break;
    case 10:
      inputCode = '9';
      break;
    case 11:
      inputCode = '-';
      break;
    case 12:
      inputCode = NUM_LOCK;
      break;
    case 13:
      inputCode = '/';
      break;
    case 14:
      inputCode = '*';
      break;
    case 15:
      inputCode = BACK_SPACE;
      break;
    }
    return inputCode;
  }

}
