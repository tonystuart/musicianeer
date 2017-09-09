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

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnStaffPrompter;
import com.example.afs.musicpad.playable.Playable;
import com.example.afs.musicpad.renderer.SongRenderer;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.RandomAccessList;

public class StaffRenderer extends SongRenderer {

  public StaffRenderer(Broker<Message> broker) {
    super(broker);
  }

  @Override
  protected void render(Song song, NavigableMap<Integer, RandomAccessList<Playable>> devicePlayables, NavigableMap<Integer, Integer> deviceChannelAssignments) {
    StaffNotator staffNotator = new StaffNotator(song, deviceChannelAssignments, devicePlayables);
    String html = staffNotator.render();
    publish(new OnStaffPrompter(html, StaffNotator.TICKS_PER_PIXEL));
  }

}
