// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import java.util.NavigableSet;

import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.player.PlayerDetail;
import com.example.afs.musicpad.task.Message;
import com.example.afs.musicpad.task.ServiceTask.Service;

public class Services {

  public static final Service<Integer> getBackgroundVelocity = new Service<Integer>() {
  };

  public static final Service<CurrentSong> getCurrentSong = new Service<CurrentSong>() {
  };

  public static final Service<NavigableSet<Integer>> getDeviceIndexes = new Service<NavigableSet<Integer>>() {
  };

  public static final Service<Integer> getPercentMasterGain = new Service<Integer>() {
  };

  public static final Service<MidiLibrary> getMidiLibrary = new Service<MidiLibrary>() {
  };

  public static Service<PlayerDetail> getPlayerDetail = new Service<PlayerDetail>() {
  };

  public static final Service<Message> getRenderingState = new Service<Message>() {
  };

  public static final Service<Integer> getPercentTempo = new Service<Integer>() {
  };
}
