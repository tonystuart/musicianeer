// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import com.example.afs.musicpad.device.common.InputMapping;
import com.example.afs.musicpad.renderer.Notator.KeyCap;
import com.example.afs.musicpad.renderer.Notator.Slice;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class AlphaMapping implements InputMapping {

  @Override
  public int onDown(int inputCode) {
    return -1;
  }

  @Override
  public void onUp(int inputCode) {
  }

  @Override
  public String toKeyCap(int midiNote) {
    return "";
  }

  @Override
  public RandomAccessList<KeyCap> toKeyCaps(RandomAccessList<Slice> slices) {
    return new DirectList<>();
  }

}
