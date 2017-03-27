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
import com.example.afs.musicpad.device.InputDevice;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;

public class SongDrumPlayer extends Player {

  public SongDrumPlayer(Synthesizer synthesizer, Song currentSong, InputDevice inputDevice) {
    super(synthesizer, Midi.DRUM);
    updateInputDevice(inputDevice);
  }

  @Override
  public int getUniqueCount() {
    return 0;
  }

  @Override
  public void play(Action action, int buttonIndex) {
  }

  @Override
  public void updateInputDevice(InputDevice inputDevice) {
  }

}
