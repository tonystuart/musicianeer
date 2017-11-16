// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import com.example.afs.musicpad.song.Note;

public class NumericPlayableMap extends QwertyPlayableMap {

  public NumericPlayableMap(Iterable<Note> notes, OutputType outputType) {
    super(notes, outputType, "123456789", " 0/*-+");
  }

}
