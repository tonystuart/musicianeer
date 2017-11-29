// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.service;

import java.io.File;
import java.util.NavigableSet;

import com.example.afs.musicpad.player.PlayerDetail;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.ServiceTask.Service;
import com.example.afs.musicpad.util.RandomAccessList;

public class Services {

  public static final Service<Integer> getBackgroundVelocity = new Service<Integer>() {
  };

  public static final Service<Song> getCurrentSong = new Service<Song>() {
  };

  public static final Service<Integer> getMasterGain = new Service<Integer>() {
  };

  public static final Service<RandomAccessList<File>> getMidiFiles = new Service<RandomAccessList<File>>() {
  };

  public static Service<PlayerDetail> getPlayerDetail = new Service<PlayerDetail>() {
  };

  public static final Service<Integer> getTempo = new Service<Integer>() {
  };

  public static final Service<NavigableSet<Integer>> getDeviceIndexes = new Service<NavigableSet<Integer>>() {
  };
}
