// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

import com.example.afs.musicpad.renderer.Notator.KeyCap;
import com.example.afs.musicpad.renderer.Notator.Slice;
import com.example.afs.musicpad.util.RandomAccessList;

public interface InputMapping {

  int onDown(int inputCode);

  void onUp(int inputCode);

  String toKeyCap(int midiNote);

  RandomAccessList<KeyCap> toKeyCaps(RandomAccessList<Slice> slices);

}
