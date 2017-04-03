// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.player.AllNotePlayer;
import com.example.afs.musicpad.util.Broker;

public class MidiDeviceHandler extends DeviceHandler {

  public MidiDeviceHandler(Broker<Message> broker, Synthesizer synthesizer) {
    super(broker, synthesizer, new MidiMapping(), new AllNotePlayer(synthesizer, 0));
  }

}
