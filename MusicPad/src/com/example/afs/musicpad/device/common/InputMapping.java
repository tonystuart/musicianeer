// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

public interface InputMapping {

  int NO_NOTE_FOR_CODE = -1;
  int NO_CODE_FOR_NOTE = -1;

  int toInputCode(int noteIndex);

  int toMidiNote(int noteIndex);

  int toNoteIndex(int inputCode);

}
