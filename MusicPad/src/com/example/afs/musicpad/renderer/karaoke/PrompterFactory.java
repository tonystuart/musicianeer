// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import java.util.NavigableMap;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.playable.Playable;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.util.RandomAccessList;

public class PrompterFactory {

  public interface Prompter {

    Message getMessage();

  }

  private enum PrompterType {
    KARAOKE, STAFF
  }

  private PrompterFactory.PrompterType prompterType = PrompterType.KARAOKE;

  public PrompterFactory.Prompter createPrompter(Song song, NavigableMap<Integer, RandomAccessList<Playable>> devicePlayables) {
    switch (prompterType) {
    case KARAOKE:
      return new KaraokePrompter(song, devicePlayables);
    case STAFF:
      return new StaffPrompter(song, devicePlayables);
    default:
      throw new UnsupportedOperationException();
    }

  }

  public void selectNext() {
    if (prompterType == PrompterType.KARAOKE) {
      prompterType = PrompterType.STAFF;
    } else {
      prompterType = PrompterType.KARAOKE;
    }
  }
}