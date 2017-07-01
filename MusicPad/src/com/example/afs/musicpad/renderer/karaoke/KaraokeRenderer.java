// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

import com.example.afs.musicpad.keycap.KeyCap;
import com.example.afs.musicpad.keycap.KeyCapMap;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelSelector;
import com.example.afs.musicpad.message.OnChannelUpdate;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnMidiFiles;
import com.example.afs.musicpad.message.OnPickChannel;
import com.example.afs.musicpad.message.OnPrompter;
import com.example.afs.musicpad.message.OnRenderSong;
import com.example.afs.musicpad.message.OnSongSelector;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.RandomAccessList;

public class KaraokeRenderer extends BrokerTask<Message> {

  private Song song;
  private NavigableMap<Integer, Integer> deviceChannelAssignments;
  private Map<Integer, RandomAccessList<KeyCap>> deviceKeyCaps = new HashMap<>();

  public KaraokeRenderer(Broker<Message> broker) {
    super(broker);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnMidiFiles.class, message -> doMidiFiles(message));
    subscribe(OnRenderSong.class, message -> doRenderSong(message));
    subscribe(OnPickChannel.class, message -> doPickChannel(message));
    subscribe(OnChannelUpdate.class, message -> doChannelUpdate(message));
  }

  private boolean allDeviceKeyCapsAvailable() {
    if (deviceChannelAssignments == null) {
      return false;
    }
    for (Integer deviceIndex : deviceChannelAssignments.keySet()) {
      if (!deviceKeyCaps.containsKey(deviceIndex)) {
        return false;
      }
    }
    return true;
  }

  private void doChannelUpdate(OnChannelUpdate message) {
    int deviceIndex = message.getDeviceIndex();
    KeyCapMap keyCapMap = message.getKeyCapMap();
    RandomAccessList<KeyCap> keyCaps = keyCapMap.getKeyCaps();
    deviceKeyCaps.put(deviceIndex, keyCaps);
    renderWhenReady();
  }

  private void doCommand(OnCommand message) {
    switch (message.getCommand()) {
    case DETACH:
      doDetach(message.getParameter());
      break;
    case SONG:
      doSong(message.getParameter());
      break;
    default:
      break;
    }
  }

  private void doDetach(int deviceIndex) {
    deviceKeyCaps.remove(deviceIndex);
    renderWhenReady();
  }

  private void doMidiFiles(OnMidiFiles message) {
    RandomAccessList<File> midiFiles = message.getMidiFiles();
    Songs songs = new Songs(midiFiles);
    String html = songs.render();
    publish(new OnSongSelector(html));
  }

  private void doPickChannel(OnPickChannel message) {
    Channels channels = new Channels(message.getSong(), message.getDeviceIndex());
    String html = channels.render();
    publish(new OnChannelSelector(html));
  }

  private void doRenderSong(OnRenderSong message) {
    song = message.getSong();
    deviceChannelAssignments = message.getDeviceChannelAssignments();
    renderWhenReady();
  }

  private void doSong(int songIndex) {
    deviceChannelAssignments = null;
  }

  private void renderWhenReady() {
    if (song != null && allDeviceKeyCapsAvailable()) {
      Prompter prompter = new Prompter(song, deviceKeyCaps);
      String html = prompter.render();
      getBroker().publish(new OnPrompter(html));
    }
  }

}
