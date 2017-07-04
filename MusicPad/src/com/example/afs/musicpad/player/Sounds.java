// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.example.afs.musicpad.device.common.DeviceHandler.OutputType;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.Count;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Sounds implements Iterable<Sound> {

  public static class SoundCount extends Count<Sound> {
    public SoundCount(Sound value) {
      super(value);
    }

    @Override
    public int compareTo(Count<Sound> that) {
      return -super.compareTo(that); // descending sort order
    }
  }

  private RandomAccessList<Sound> sounds = new DirectList<>();

  public Sounds(OutputType outputType, Iterable<Note> notes) {
    Sound sound = null;
    int lastIndex = -1;
    for (Note note : notes) {
      int index;
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

  public Map<Sound, SoundCount> getUniqueSoundCounts() {
    Map<Sound, SoundCount> uniqueSoundCounts = new HashMap<>();
    for (Sound sound : sounds) {
      SoundCount count = uniqueSoundCounts.get(sound);
      if (count == null) {
        count = new SoundCount(sound);
        uniqueSoundCounts.put(sound, count);
      }
      count.increment();
    }
    return uniqueSoundCounts;
  }

  @Override
  public Iterator<Sound> iterator() {
    return sounds.iterator();
  }

}
