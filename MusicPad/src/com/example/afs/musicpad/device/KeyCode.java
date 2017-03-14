// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device;


public class KeyCode {

  public static int toCharCode(int keyCode) {
    int charCode = -1;
    switch (keyCode) {
    case 69:
      charCode = DeviceReader.NUM_LOCK;
      break;
    case 98:
      charCode = '/';
      break;
    case 55:
      charCode = '*';
      break;
    case 14:
      charCode = DeviceReader.BACK_SPACE;
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
      charCode = DeviceReader.ENTER;
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

}
