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
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.example.afs.musicpad.html.Option;
import com.example.afs.musicpad.html.Template;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelDetails;
import com.example.afs.musicpad.message.OnChannelUpdate;
import com.example.afs.musicpad.message.OnChannels;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceDetached;
import com.example.afs.musicpad.message.OnKaraokePrompter;
import com.example.afs.musicpad.message.OnMidiFiles;
import com.example.afs.musicpad.message.OnPickChannel;
import com.example.afs.musicpad.message.OnRenderSong;
import com.example.afs.musicpad.message.OnSampleChannel;
import com.example.afs.musicpad.message.OnSampleSong;
import com.example.afs.musicpad.message.OnSongDetails;
import com.example.afs.musicpad.message.OnSongs;
import com.example.afs.musicpad.message.OnTemplates;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.playable.Playable;
import com.example.afs.musicpad.playable.PlayableMap;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.RandomAccessList;

public class KaraokeRenderer extends BrokerTask<Message> {

  private Song song;
  private NavigableMap<Integer, Integer> deviceChannelAssignments = new TreeMap<>();
  private NavigableMap<Integer, RandomAccessList<Playable>> devicePlayables = new TreeMap<>();

  public KaraokeRenderer(Broker<Message> broker) {
    super(broker);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnMidiFiles.class, message -> doMidiFiles(message));
    subscribe(OnRenderSong.class, message -> doRenderSong(message));
    subscribe(OnSampleSong.class, message -> doSampleSong(message));
    subscribe(OnPickChannel.class, message -> doPickChannel(message));
    subscribe(OnSampleChannel.class, message -> doSampleChannel(message));
    subscribe(OnChannelUpdate.class, message -> doChannelUpdate(message));
    subscribe(OnDeviceDetached.class, message -> doDeviceDetached(message));
  }

  @Override
  public void start() {
    super.start();
    publishTemplates();
  }

  private boolean allDevicePlayablesAvailable() {
    if (deviceChannelAssignments.size() == 0) {
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
    case SELECT_SONG:
      doSelectSong(message.getParameter());
      break;
    default:
      break;
    }
  }

  private void doDeviceDetached(OnDeviceDetached message) {
    if (devicePlayables.size() == 1) {
      System.err.println("KaraokeRenderer: suppressing removal of last playable for testing purposes");
      return;
    }
    int deviceIndex = message.getDeviceIndex();
    devicePlayables.remove(deviceIndex);
    deviceChannelAssignments.remove(deviceIndex);
    renderWhenReady();
  }

  private void doMidiFiles(OnMidiFiles message) {
    RandomAccessList<File> midiFiles = message.getMidiFiles();
    SongSelector songSelector = new SongSelector(midiFiles);
    String html = songSelector.render();
    publish(new OnSongs(html));
  }

  private void doPickChannel(OnPickChannel message) {
    ChannelSelector channelSelector = new ChannelSelector(message.getSong(), message.getDeviceIndex(), message.getDeviceChannelAssignments());
    String html = channelSelector.render();
    publish(new OnChannels(html));
  }

  private void doRenderSong(OnRenderSong message) {
    song = message.getSong();
    deviceChannelAssignments.clear();
    deviceChannelAssignments.putAll(message.getDeviceChannelAssignments());
    renderWhenReady();
  }

  private void doSampleChannel(OnSampleChannel message) {
    Song song = message.getSong();
    int channel = message.getChannel();
    ChannelDetails channelDetails = new ChannelDetails(song, channel);
    String html = channelDetails.render();
    publish(new OnChannelDetails(html));
  }

  private void doSampleSong(OnSampleSong message) {
    Song song = message.getSong();
    SongDetails songDetails = new SongDetails(song);
    String html = songDetails.render();
    publish(new OnSongDetails(html));
  }

  private void doSelectSong(int songIndex) {
    deviceChannelAssignments.clear();
  }

  private String getProgramOptions() {
    Template template = new Template("#program-options");
    for (int i = 0; i < Midi.PROGRAMS; i++) {
      Option option = new Option(Instruments.getProgramName(i), i);
      template.appendChild(option);
    }
    String programOptions = template.render();
    return programOptions;
  }

  private void publishTemplates() {
    List<String> templates = new LinkedList<>();
    templates.add(getProgramOptions());
    getBroker().publish(new OnTemplates(templates));
  }

  private void renderWhenReady() {
    if (song != null && allDevicePlayablesAvailable()) {
      KarokeNotator karokeNotator = new KarokeNotator(song, devicePlayables);
      String html = karokeNotator.render();
      publish(new OnKaraokePrompter(html));
    }
  }

}
