// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import com.example.afs.musicpad.device.common.DeviceHandler.OutputType;
import com.example.afs.musicpad.keycap.KeyCap;
import com.example.afs.musicpad.keycap.KeyCapMap;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class MidiKeyCapMap implements KeyCapMap {

  private RandomAccessList<Sound> sounds = new DirectList<>();

  public MidiKeyCapMap(Iterable<Note> notes, OutputType outputType) {
    int index;
    int lastIndex = -1;
    Sound sound = null;
    for (Note note : notes) {
      if (outputType == OutputType.NORMAL) {
        index = note.getStartIndex();
      } else {
        index = note.getEndIndex();
      }
      if (index != lastIndex) {
        lastIndex = index;
        if (sound != null) {
          sounds.add(sound);
        }
        sound = new Sound();
      }
      sound.add(note);
    }
  }

  @Override
  public RandomAccessList<KeyCap> getKeyCaps() {
    DirectList<KeyCap> keyCaps = new DirectList<>();
    for (Sound sound : sounds) {
      keyCaps.add(new KeyCap(sound, sound.getName()));
    }
    return keyCaps;
  }

  @Override
  public Sound onDown(int inputCode) {
    return new Sound(new Note.NoteBuilder().withMidiNote(inputCode).create());
  }

  @Override
  public void onUp(int inputCode) {
  }

}
