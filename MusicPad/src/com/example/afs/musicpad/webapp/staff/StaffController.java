// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.staff;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.example.afs.musicpad.message.OnRenderSong;
import com.example.afs.musicpad.message.OnSampleChannel;
import com.example.afs.musicpad.message.OnSampleSong;
import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.message.OnShadowUpdate.Action;
import com.example.afs.musicpad.player.PlayerDetail;
import com.example.afs.musicpad.service.PlayerDetailService;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.MessageBroker;

public class StaffController extends ControllerTask {

  private boolean initialized;
  private StaffView staffView;

  public StaffController(MessageBroker broker) {
    super(broker);
    staffView = new StaffView(this);
    subscribe(OnRenderSong.class, message -> doRenderSong(message));
    subscribe(OnSampleSong.class, message -> doSampleSong(message));
    subscribe(OnSampleChannel.class, message -> doSampleChannel(message));
  }

  @Override
  protected synchronized void doLoad() {
    if (!initialized) {
      initialized = true;
    }
    addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "body", staffView.render()));
  }

  private void doRenderSong(OnRenderSong message) {
    NavigableMap<Integer, PlayerDetail> devicePlayerDetail = new TreeMap<>();
    NavigableMap<Integer, Integer> deviceChannelAssignments = message.getDeviceChannelAssignments();
    for (Entry<Integer, Integer> entry : deviceChannelAssignments.entrySet()) {
      Integer deviceIndex = entry.getKey();
      PlayerDetail playerDetail = request(new PlayerDetailService(deviceIndex));
      devicePlayerDetail.put(deviceIndex, playerDetail);
    }
    staffView.renderSong(message.getSong(), devicePlayerDetail);
  }

  private void doSampleChannel(OnSampleChannel message) {
  }

  private void doSampleSong(OnSampleSong message) {
  }

}
