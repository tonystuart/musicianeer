// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import com.example.afs.musicpad.device.common.InputMap;
import com.example.afs.musicpad.task.MessageBroker;

public class BeatStepConfiguration extends MidiConfiguration {

  public BeatStepConfiguration(MessageBroker broker, int deviceIndex) {
    super(broker, deviceIndex);
    this.bankMap = new InputMap( //
        new int[] {
            36, // 9
            37, // 10
            38, // 11
            39, // 12
            44, // 1
            45, // 2
            46, // 3
            47 // 4
        }, new String[] {
            "9",
            "10",
            "11",
            "12",
            "1",
            "2",
            "3",
            "4"
        });//
    this.noteMap = new InputMap( //
        new int[] {
            40, // 13
            41, // 14
            42, // 15
            43, // 16
            48, // 5
            49, // 6
            50, // 7
            51 // 8
        }, new String[] {
            "13",
            "14",
            "15",
            "16",
            "5",
            "6",
            "7",
            "8"
        });//
  }

}
