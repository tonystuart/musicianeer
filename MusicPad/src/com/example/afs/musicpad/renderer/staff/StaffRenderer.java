// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.staff;

import java.util.NavigableMap;
import java.util.TreeMap;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelUpdate;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceDetached;
import com.example.afs.musicpad.message.OnRenderSong;
import com.example.afs.musicpad.message.OnStaffPrompter;
import com.example.afs.musicpad.playable.Playable;
import com.example.afs.musicpad.playable.PlayableMap;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.RandomAccessList;

public class StaffRenderer extends BrokerTask<Message> {

  private Song song;
  private NavigableMap<Integer, Integer> deviceChannelAssignments;
  private NavigableMap<Integer, RandomAccessList<Playable>> devicePlayables = new TreeMap<>();

  public StaffRenderer(Broker<Message> broker) {
    super(broker);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnRenderSong.class, message -> doRenderSong(message));
    subscribe(OnChannelUpdate.class, message -> doChannelUpdate(message));
    subscribe(OnDeviceDetached.class, message -> doDeviceDetached(message));
  }

  private boolean allDevicePlayablesAvailable() {
    if (deviceChannelAssignments == null) {
      return false;
    }
    for (Integer deviceIndex : deviceChannelAssignments.keySet()) {
      if (!devicePlayables.containsKey(deviceIndex)) {
        return false;
      }
    }
    return true;
  }

  private void doChannelUpdate(OnChannelUpdate message) {
    int deviceIndex = message.getDeviceIndex();
    PlayableMap playableMap = message.getPlayableMap();
    RandomAccessList<Playable> playables = playableMap.getPlayables();
    devicePlayables.put(deviceIndex, playables);
    renderWhenReady();
  }

  private void doCommand(OnCommand message) {
    switch (message.getCommand()) {
    default:
      break;
    }
  }

  private void doDeviceDetached(OnDeviceDetached message) {
    if (devicePlayables.size() == 1) {
      System.err.println("StaffRenderer: suppressing removal of last playable for testing purposes");
      return;
    }
    int deviceIndex = message.getDeviceIndex();
    devicePlayables.remove(deviceIndex);
    if (deviceChannelAssignments != null) {
      deviceChannelAssignments.remove(deviceIndex);
    }
    renderWhenReady();
  }

  private void doRenderSong(OnRenderSong message) {
    song = message.getSong();
    deviceChannelAssignments = message.getDeviceChannelAssignments();
    renderWhenReady();
  }

  private void renderWhenReady() {
    if (song != null && allDevicePlayablesAvailable()) {
      StaffNotator staffNotator = new StaffNotator(song, deviceChannelAssignments, devicePlayables);
      String html = staffNotator.render();
      publish(new OnStaffPrompter(html, StaffNotator.TICKS_PER_PIXEL));
    }
  }

}
