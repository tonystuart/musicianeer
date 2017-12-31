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

import com.example.afs.musicpad.device.common.InputMap;
import com.example.afs.musicpad.message.OnRenderSong;
import com.example.afs.musicpad.message.OnSampleChannel;
import com.example.afs.musicpad.message.OnSampleSong;
import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.message.OnShadowUpdate.Action;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.Playable;
import com.example.afs.musicpad.player.PlayableMap;
import com.example.afs.musicpad.player.PlayableMap.OutputType;
import com.example.afs.musicpad.player.PlayerDetail;
import com.example.afs.musicpad.service.PlayerDetailService;
import com.example.afs.musicpad.service.Services;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.Message;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.util.DelayTimer;
import com.example.afs.musicpad.util.RandomAccessList;

public class StaffController extends ControllerTask {

  private class DeferredSongSampler {

    private Song song;
    private DelayTimer delayTimer = new DelayTimer(() -> onTimeout());

    public void cancel() {
      song = null;
    }

    public void defer(Song song) {
      this.song = song;
      System.out.println("Deferring sampling of " + song);
      delayTimer.delay(2000);
    }

    private void onTimeout() {
      if (song != null) {
        System.out.println("Sampling " + song);
        sampleSong(song);
      }
    }

  }

  private boolean initialized;
  private StaffView staffView;
  private DeferredSongSampler deferredSongSampler = new DeferredSongSampler();

  public StaffController(MessageBroker broker) {
    super(broker);
    staffView = new StaffView(this);
    subscribe(OnRenderSong.class, message -> doRenderSong(message));
    subscribe(OnSampleSong.class, message -> doSampleSong(message));
    subscribe(OnSampleChannel.class, message -> doSampleChannel(message));
  }

  @Override
  protected void doInput(String id, String value) {
    if (id.equals("zoom-slider")) {
      staffView.zoom(Integer.parseInt(value));
    }
  }

  @Override
  protected synchronized void doLoad() {
    if (!initialized) {
      initialized = true;
      Message renderingState = request(Services.getRenderingState);
      tsGetInputQueue().add(renderingState);
    }
    addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "body", staffView.render()));
  }

  private void doRenderSong(OnRenderSong message) {
    deferredSongSampler.cancel();
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
    deferredSongSampler.cancel();
    Song song = message.getSong();
    int channel = message.getChannel();
    if (song.getChannelNoteCount(channel) > 0) {
      NavigableMap<Integer, PlayerDetail> devicePlayerDetail = new TreeMap<>();
      InputMap inputMap = new InputMap("");
      Iterable<Note> channelNotes = song.getChannelNotes(channel);
      PlayableMap playableMap = new PlayableMap(inputMap, inputMap, channelNotes, OutputType.TICK);
      RandomAccessList<Playable> playables = playableMap.getPlayables();
      devicePlayerDetail.put(message.getDeviceIndex(), new PlayerDetail(playables, channel, 0));
      staffView.renderSong(song, devicePlayerDetail);
    }
  }

  private void doSampleSong(OnSampleSong message) {
    deferredSongSampler.defer(message.getSong());
  }

  private void sampleSong(Song song) {
    NavigableMap<Integer, PlayerDetail> devicePlayerDetail = new TreeMap<>();
    InputMap inputMap = new InputMap("");
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (song.getChannelNoteCount(channel) > 0) {
        Iterable<Note> channelNotes = song.getChannelNotes(channel);
        PlayableMap playableMap = new PlayableMap(inputMap, inputMap, channelNotes, OutputType.TICK);
        RandomAccessList<Playable> playables = playableMap.getPlayables();
        devicePlayerDetail.put(channel, new PlayerDetail(playables, channel, 0));
      }
    }
    staffView.renderSong(song, devicePlayerDetail);
  }

}
