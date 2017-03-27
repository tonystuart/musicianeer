// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device;

public abstract class InputDevice {

  public static final char ENTER = 'E';

  public abstract int fromIndex(int index);

  public String fromIndexToSequence(int buttonIndex) {
    String keySequence;
    int page = buttonIndex / getButtonPageSize();
    int index = buttonIndex % getButtonPageSize();
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

  public abstract int getButtonPageSize();

  public abstract int getButtonTotalSize();

  public abstract int toCharCode(int keyCode);

  public abstract int toIndex(int charCode);

}
