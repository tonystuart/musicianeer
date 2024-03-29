// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.main;

import com.example.afs.musicianeer.device.midi.MidiHandle;
import com.example.afs.musicianeer.message.OnSetAccompanimentType.AccompanimentType;
import com.example.afs.musicianeer.message.OnSongInfo;
import com.example.afs.musicianeer.midi.MidiLibrary;
import com.example.afs.musicianeer.task.ServiceTask.Service;
import com.example.afs.musicianeer.util.RandomAccessList;

public class Services {

  public static final Service<AccompanimentType> getAccompanimentType = new Service<AccompanimentType>() {
  };

  public static Service<CurrentPrograms> getCurrentPrograms = new Service<CurrentPrograms>() {
  };

  public static final Service<CurrentSong> getCurrentSong = new Service<CurrentSong>() {
  };

  public static final Service<Iterable<MidiHandle>> getMidiHandles = new Service<Iterable<MidiHandle>>() {
  };

  public static final Service<Integer> getPercentMasterGain = new Service<Integer>() {
  };

  public static final Service<Integer> getPercentTempo = new Service<Integer>() {
  };

  public static final Service<RandomAccessList<OnSongInfo>> getSongInfoList = new Service<RandomAccessList<OnSongInfo>>() {
  };

  public static final Service<SynthesizerSettings> getSynthesizerSettings = new Service<SynthesizerSettings>() {
  };

  public static final Service<MidiLibrary> refreshMidiLibrary = new Service<MidiLibrary>() {
  };

}
