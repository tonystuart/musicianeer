// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import com.example.afs.musicpad.player.Playable;
import com.example.afs.musicpad.player.PlayableMap;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.player.Sounds;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class MidiPlayableMap implements PlayableMap {

  private Sounds sounds;

  public MidiPlayableMap(Iterable<Note> notes, OutputType outputType) {
    // These are used for notation, not for playing
    sounds = new Sounds(outputType, notes);
  }

  @Override
  public RandomAccessList<Playable> getPlayables() {
    DirectList<Playable> playables = new DirectList<>();
    for (Sound sound : sounds) {
      playables.add(new Playable(sound, sound.getName()));
    }
    return playables;
  }

  @Override
  public Sound onDown(int inputCode) {
    return new Sound(new Note.NoteBuilder().withMidiNote(inputCode).create());
  }

  @Override
  public void onUp(int inputCode) {
  }

}
