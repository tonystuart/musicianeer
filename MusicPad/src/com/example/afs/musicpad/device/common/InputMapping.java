// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

public abstract class InputMapping {

  public String fromIndexToSequence(int buttonIndex) {
    String keySequence;
    int charCode = fromNoteIndex(buttonIndex);
    if (charCode == -1) {
      keySequence = "?";
    } else {
      keySequence = Character.toString((char) charCode);
    }
    return keySequence;
  }

  public abstract int fromNoteIndex(int noteIndex);

  public abstract int toCharCode(int keyCode);

  public abstract int toNoteIndex(int charCode);

}
