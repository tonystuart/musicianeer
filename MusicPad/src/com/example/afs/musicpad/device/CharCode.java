// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device;

public class CharCode {

  public static final int PAGE_SIZE = 16;
  public static final int TOTAL_SIZE = PAGE_SIZE * 2;

  public static int fromIndex(int index) {
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
      charCode = DeviceReader.ENTER;
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
      charCode = DeviceReader.NUM_LOCK;
      break;
    case 13:
      charCode = '/';
      break;
    case 14:
      charCode = '*';
      break;
    case 15:
      charCode = DeviceReader.BACK_SPACE;
      break;
    }
    return charCode;
  }

  public static int toIndex(int charCode) {
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
    case DeviceReader.ENTER:
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
    case DeviceReader.NUM_LOCK:
      index = 12;
      break;
    case '/':
      index = 13;
      break;
    case '*':
      index = 14;
      break;
    case DeviceReader.BACK_SPACE:
      index = 15;
      break;
    }
    return index;
  }

  public static String fromIndexToSequence(int buttonIndex) {
    String keySequence;
    int page = buttonIndex / PAGE_SIZE;
    int index = buttonIndex % PAGE_SIZE;
    int charCode = fromIndex(index);
    if (page == 0) {
      keySequence = Character.toString((char) charCode);
    } else if (page == 1) {
      keySequence = "0+" + (char) charCode;
    } else {
      keySequence = "?+" + (char) charCode;
    }
    return keySequence;
  }

}
