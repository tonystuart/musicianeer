// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.device.common.Device;
import com.example.afs.musicpad.device.common.InputMapping;
import com.example.afs.musicpad.device.midi.MidiMapping;
import com.example.afs.musicpad.device.qwerty.AlphaMapping;
import com.example.afs.musicpad.device.qwerty.NumericMapping;
import com.example.afs.musicpad.song.Song;

public class PlayerFactory {

  private Synthesizer synthesizer;

  public PlayerFactory(Synthesizer synthesizer) {
    this.synthesizer = synthesizer;
  }

  public Player createPlayer(Song song, Device device) {
    Player player = null;
    int concurrency = song.getConcurrency(device.getChannel());
    InputMapping inputMapping = device.getInputMapping();
    if (inputMapping instanceof MidiMapping) {
      player = new NotePlayer(synthesizer, song, device);
    } else if (inputMapping instanceof AlphaMapping) {
      if (concurrency < 110) {
        player = new NotePlayer(synthesizer, song, device);
      } else {
        player = new ChordPlayer(synthesizer, song, device);
      }
    } else if (inputMapping instanceof NumericMapping) {
      player = new ChordPlayer(synthesizer, song, device);
    }
    return player;
  }

}
