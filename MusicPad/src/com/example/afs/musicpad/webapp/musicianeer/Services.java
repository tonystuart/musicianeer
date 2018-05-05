// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.task.ServiceTask.Service;
import com.example.afs.musicpad.webapp.musicianeer.SongInfoFactory.SongInfo;

public class Services {

  public static final Service<CurrentSong> getCurrentSong = new Service<CurrentSong>() {
  };

  public static final Service<Integer> getPercentMasterGain = new Service<Integer>() {
  };

  public static final Service<MidiLibrary> getMidiLibrary = new Service<MidiLibrary>() {
  };

  public static final Service<Integer> getPercentTempo = new Service<Integer>() {
  };

  public static final Service<Iterable<SongInfo>> getSongInfoList = new Service<Iterable<SongInfo>>() {
  };
}
