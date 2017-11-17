// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.midi;

import com.example.afs.musicpad.device.midi.MidiPlayableMap;
import com.example.afs.musicpad.song.Note;

public class BeatStepPlayableMap extends MidiPlayableMap {

  public BeatStepPlayableMap(Iterable<Note> notes, OutputType outputType) {
    super(notes, outputType, new int[] {
        39, // 12
        40,
        41,
        42,
        43,
        47, // 4
        48,
        49,
        50,
        51
    }, new int[] {
        36, // 9
        37,
        38,
        44, // 1
        45,
        46
    });
  }

  @Override
  protected String getBankLegend(int bankIndex) {
    return new String[] {
        "9",
        "10",
        "11",
        "1",
        "2",
        "3"
    }[bankIndex] + "+";
  }

  @Override
  protected String getNoteLegend(int noteIndex) {
    return new String[] {
        "12",
        "13",
        "14",
        "15",
        "16",
        "4",
        "5",
        "6",
        "7",
        "8"
    }[noteIndex];
  }

}
