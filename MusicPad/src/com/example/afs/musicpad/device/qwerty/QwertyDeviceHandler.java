// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.player.KeyNotePlayer;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.SongNotePlayer;
import com.example.afs.musicpad.theory.Keys;
import com.example.afs.musicpad.util.Broker;

public class QwertyDeviceHandler extends DeviceHandler {

  public QwertyDeviceHandler(Broker<Message> broker, Synthesizer synthesizer) {
    super(broker, synthesizer, new AlphaMapping());
  }

  @Override
  protected Player createDefaultPlayer() {
    return new KeyNotePlayer(synthesizer, Keys.CMajor, 0);
  }

  @Override
  protected Player createSongNotePlayer(int channel) {
    return new SongNotePlayer(synthesizer, currentSong, channel, inputMapping);
  }

}
