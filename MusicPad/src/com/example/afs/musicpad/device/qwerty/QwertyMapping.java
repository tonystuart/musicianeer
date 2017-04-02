// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import java.awt.event.KeyEvent;

import com.example.afs.musicpad.device.common.InputMapping;

public abstract class QwertyMapping extends InputMapping {

  // http://www.fileformat.info/
  protected static final int ENTER = '\u23ce';
  protected static final int BACK_SPACE = '\u2190';
  protected static final int NUM_LOCK = '#';

  @Override
  public int toInputCode(int keyCode) {
    int inputCode = QwertyKeyCodes.inputCodes[keyCode];
    return inputCode;
  }

  @Override
  public int toNoteIndex(int inputCode) {
    int noteIndex = -1;
    if (inputCode >= 'A' && inputCode <= 'Z') {
      noteIndex = inputCode - 'A';
    } else {
      switch (inputCode) {
      case '1':
        noteIndex = 0;
        break;
      case '2':
        noteIndex = 1;
        break;
      case '3':
        noteIndex = 2;
        break;
      case ENTER:
      case KeyEvent.VK_ENTER:
        noteIndex = 3;
        break;
      case '4':
        noteIndex = 4;
        break;
      case '5':
        noteIndex = 5;
        break;
      case '6':
        noteIndex = 6;
        break;
      case '+':
        noteIndex = 7;
        break;
      case '7':
        noteIndex = 8;
        break;
      case '8':
        noteIndex = 9;
        break;
      case '9':
        noteIndex = 10;
        break;
      case '-':
        noteIndex = 11;
        break;
      case NUM_LOCK:
      case KeyEvent.VK_NUM_LOCK:
        noteIndex = 12;
        break;
      case '/':
        noteIndex = 13;
        break;
      case '*':
        noteIndex = 14;
        break;
      case BACK_SPACE:
      case KeyEvent.VK_BACK_SPACE:
        noteIndex = 15;
        break;
      }
    }
    return noteIndex;
  }

}
