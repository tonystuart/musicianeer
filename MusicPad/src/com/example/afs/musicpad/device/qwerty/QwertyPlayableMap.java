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

import com.example.afs.musicpad.player.AbstractPlayableMap;
import com.example.afs.musicpad.song.Note;

public class QwertyPlayableMap extends AbstractPlayableMap {

  private static int[] getKeysArray(String noteKeys) {
    int length = noteKeys.length();
    int[] keysArray = new int[length];
    for (int index = 0; index < length; index++) {
      keysArray[index] = noteKeys.charAt(index);
    }
    return keysArray;
  }

  public QwertyPlayableMap(Iterable<Note> notes, OutputType outputType, String noteInputCodes, String bankInputCodes) {
    super(notes, outputType, getKeysArray(noteInputCodes), getKeysArray(bankInputCodes));
  }

  @Override
  protected String getBankLegend(int bankIndex) {
    return KeyEvent.getKeyText(bankInputCodes[bankIndex]) + "+";
  }

  @Override
  protected String getNoteLegend(int noteIndex) {
    return KeyEvent.getKeyText(noteInputCodes[noteIndex]);
  }

}