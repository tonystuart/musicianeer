// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.parser;

import java.io.File;

import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;

public class SongBuilder {

  public Song createSong(File file) {
    Song song = new Song(file.getName());
    SongListener songListener = new SongListener(song);
    MidiParser midiParser = new MidiParser(songListener, Default.TICKS_PER_BEAT);
    midiParser.parse(file.getPath());
    return song;
  }

}
