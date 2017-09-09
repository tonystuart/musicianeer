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

import com.example.afs.musicpad.html.Option;
import com.example.afs.musicpad.html.Template;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelDetails;
import com.example.afs.musicpad.message.OnChannels;
import com.example.afs.musicpad.message.OnKaraokePrompter;
import com.example.afs.musicpad.message.OnMidiFiles;
import com.example.afs.musicpad.message.OnPickChannel;
import com.example.afs.musicpad.message.OnSampleChannel;
import com.example.afs.musicpad.message.OnSampleSong;
import com.example.afs.musicpad.message.OnSongDetails;
import com.example.afs.musicpad.message.OnSongs;
import com.example.afs.musicpad.message.OnTemplates;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.playable.Playable;
import com.example.afs.musicpad.renderer.SongRenderer;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.RandomAccessList;

public class KaraokeRenderer extends SongRenderer {

  public KaraokeRenderer(Broker<Message> broker) {
    super(broker);
    subscribe(OnMidiFiles.class, message -> doMidiFiles(message));
    subscribe(OnSampleSong.class, message -> doSampleSong(message));
    subscribe(OnPickChannel.class, message -> doPickChannel(message));
    subscribe(OnSampleChannel.class, message -> doSampleChannel(message));
  }

  @Override
  public void start() {
    super.start();
    publishTemplates();
  }

  @Override
  protected void render(Song song, NavigableMap<Integer, RandomAccessList<Playable>> devicePlayables, NavigableMap<Integer, Integer> deviceChannelAssignments) {
    KarokeNotator karokeNotator = new KarokeNotator(song, devicePlayables);
    String html = karokeNotator.render();
    publish(new OnKaraokePrompter(html));
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

}
