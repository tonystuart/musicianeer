// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad;

import java.io.File;

import com.example.afs.musicpad.playable.PlayerDetail;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.ServiceTask.Service;
import com.example.afs.musicpad.util.RandomAccessList;

public class Services {

  public static final Service<Integer> getMidiVelocity = new Service<Integer>() {
  };

  public static final Service<Song> GetCurrentSong = new Service<Song>() {
  };

  public static final Service<RandomAccessList<File>> GetMidiFiles = new Service<RandomAccessList<File>>() {
  };

  public static Service<PlayerDetail> GetPlayerDetail = new Service<PlayerDetail>() {
  };
}
