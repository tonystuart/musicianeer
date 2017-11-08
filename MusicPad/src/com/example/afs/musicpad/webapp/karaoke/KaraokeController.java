// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.karaoke;

import java.io.File;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.MidiFiles;
import com.example.afs.musicpad.html.Option;
import com.example.afs.musicpad.html.Template;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnKaraokeBandEvent;
import com.example.afs.musicpad.message.OnKaraokeBandHtml;
import com.example.afs.musicpad.message.OnKaraokeBandHtml.Action;
import com.example.afs.musicpad.message.OnRenderSong;
import com.example.afs.musicpad.message.OnSampleChannel;
import com.example.afs.musicpad.message.OnSampleSong;
import com.example.afs.musicpad.message.OnPickChannel;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.playable.Playables;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask;
import com.example.afs.musicpad.util.RandomAccessList;

public class KaraokeController extends ServiceTask {

  private KaraokeView karaokeView;
  private Random random = new Random();

  public KaraokeController(MessageBroker broker) {
    super(broker);
    karaokeView = new KaraokeView(broker);
    subscribe(OnRenderSong.class, message -> doRenderSong(message));
    subscribe(OnSampleSong.class, message -> doSampleSong(message));
    subscribe(OnPickChannel.class, message -> doPickChannel(message));
    subscribe(OnSampleChannel.class, message -> doSampleChannel(message));
    subscribe(OnKaraokeBandEvent.class, message -> doKaraokeBandEvent(message));
  }

  @Override
  public void start() {
    super.start();
    MidiFiles midiFilesResponse = request(MidiFiles.class);
    RandomAccessList<File> midiFiles = midiFilesResponse.getMidiFiles();
    karaokeView.renderSongList(midiFiles);
    pickRandomSong(midiFiles);
  }

  private void backToSongs() {
  }

  private void doClick(String id) {
    if (id.startsWith("song-index-")) {
      sampleSong(Integer.parseInt(id.substring("song-index-".length())));
    } else if (id.startsWith("channel-index-")) {
      sampleChannel(Integer.parseInt(id.substring("channel-index-".length())));
    } else {
      switch (id) {
      case "song-roulette":
        pickRandomSong();
        break;
      case "song-stop":
        stop();
        break;
      case "song-next":
        selectSong();
        break;
      case "channel-to-song":
        backToSongs();
        break;
      case "channel-stop":
        stop();
        break;
      case "channel-next":
        selectChannel();
        break;
      case "prompter-to-song":
        backToSongs();
        break;
      case "prompter-stop":
        stop();
        break;
      case "prompter-next":
        break;
      }
    }
  }

  private void doInput(String id, int value) {
    System.out.println("KaraokeController.doInput: id=" + id + ", value=" + value);
  }

  private void doKaraokeBandEvent(OnKaraokeBandEvent message) {
    switch (message.getAction()) {
    case LOAD:
      doLoad();
      break;
    case CLICK:
      doClick(message.getId());
      break;
    case INPUT:
      doInput(message.getId(), message.getValue());
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private void doLoad() {
    publish(new OnKaraokeBandHtml(Action.REPLACE_CHILDREN, "body", karaokeView.render()));
  }

  private void doRenderSong(OnRenderSong message) {
    NavigableMap<Integer, Playables> devicePlayables = new TreeMap<>();
    for (Entry<Integer, Integer> entry : message.getDeviceChannelAssignments().entrySet()) {
      Integer deviceIndex = entry.getKey();
      Playables playables = request(Playables.getPlayableDeviceKey(deviceIndex));
      devicePlayables.put(deviceIndex, playables);
    }
    karaokeView.renderSong(message.getSong(), devicePlayables);
  }

  private void doSampleChannel(OnSampleChannel message) {
    karaokeView.selectChannel(message.getSong(), message.getChannel());
  }

  private void doSampleSong(OnSampleSong message) {
    karaokeView.renderSongDetails(message.getSong());
  }

  private void doPickChannel(OnPickChannel message) {
    karaokeView.renderChannelList(message.getSong(), message.getDeviceIndex(), message.getDeviceChannelAssignments());
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

  private void pickRandomSong() {
    MidiFiles midiFilesResponse = request(MidiFiles.class);
    RandomAccessList<File> midiFiles = midiFilesResponse.getMidiFiles();
    pickRandomSong(midiFiles);
  }

  private void pickRandomSong(RandomAccessList<File> midiFiles) {
    if (midiFiles.size() > 0) {
      int songIndex = random.nextInt(midiFiles.size());
      sampleSong(songIndex);
    }
  }

  private void sampleChannel(int channelIndex) {
    publish(new OnDeviceCommand(DeviceCommand.SAMPLE_CHANNEL, karaokeView.getDeviceIndex(), channelIndex));
  }

  private void sampleSong(int songIndex) {
    karaokeView.selectSong(songIndex);
    publish(new OnCommand(Command.SAMPLE_SONG, songIndex));
  }

  private void selectChannel() {
    publish(new OnDeviceCommand(DeviceCommand.SELECT_CHANNEL, karaokeView.getDeviceIndex(), karaokeView.getChannelIndex()));
  }

  private void selectSong() {
    publish(new OnCommand(Command.SELECT_SONG, karaokeView.getSongIndex()));
  }

  private void stop() {
    publish(new OnCommand(Command.STOP, 0));
  }

}
