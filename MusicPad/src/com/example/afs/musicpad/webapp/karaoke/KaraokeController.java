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
import com.example.afs.musicpad.html.Option;
import com.example.afs.musicpad.html.Template;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnPickChannel;
import com.example.afs.musicpad.message.OnRenderSong;
import com.example.afs.musicpad.message.OnSampleChannel;
import com.example.afs.musicpad.message.OnSampleSong;
import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.message.OnShadowUpdate.Action;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.PlayerDetail;
import com.example.afs.musicpad.service.BackgroundMuteService;
import com.example.afs.musicpad.service.PlayerDetailService;
import com.example.afs.musicpad.service.PlayerVelocityService;
import com.example.afs.musicpad.service.Services;
import com.example.afs.musicpad.song.ChannelNotes;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.util.RandomAccessList;

public class KaraokeController extends ControllerTask {

  private KaraokeView karaokeView;
  private Random random = new Random();

  public KaraokeController(MessageBroker broker) {
    super(broker);
    karaokeView = new KaraokeView(this);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnDeviceCommand.class, message -> doDeviceCommand(message));
    subscribe(OnRenderSong.class, message -> doRenderSong(message));
    subscribe(OnSampleSong.class, message -> doSampleSong(message));
    subscribe(OnPickChannel.class, message -> doPickChannel(message));
    subscribe(OnSampleChannel.class, message -> doSampleChannel(message));
  }

  @Override
  public void tsStart() {
    super.tsStart();
    RandomAccessList<File> midiFiles = request(Services.getMidiFiles);
    karaokeView.renderSongList(midiFiles);
    pickRandomSong(midiFiles);
  }

  @Override
  protected void doClick(String id) {
    if (id.startsWith("song-index-")) {
      sampleSong(Integer.parseInt(id.substring("song-index-".length())));
    } else if (id.startsWith("channel-index-")) {
      sampleChannel(Integer.parseInt(id.substring("channel-index-".length())));
    } else if (id.startsWith("tick-")) {
      System.out.println("doClick: id=" + id);
      seek(Integer.parseInt(id.substring("tick-".length())));
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
        play();
        break;
      }
    }
  }

  @Override
  protected void doInput(String id, String value) {
    if (id.startsWith("background-mute-")) {
      publish(new OnDeviceCommand(DeviceCommand.MUTE_BACKGROUND, Integer.parseInt(id.substring("background-mute-".length())), Integer.parseInt(value)));
    } else if (id.startsWith("device-velocity-")) {
      publish(new OnDeviceCommand(DeviceCommand.VELOCITY, Integer.parseInt(id.substring("device-velocity-".length())), Integer.parseInt(value)));
    } else {
      switch (id) {
      case "background-velocity":
        publish(new OnCommand(Command.SET_BACKGROUND_VELOCITY, Integer.parseInt(value)));
        break;
      case "master-gain":
        publish(new OnCommand(Command.SET_MASTER_GAIN, Integer.parseInt(value)));
        break;
      case "tempo":
        publish(new OnCommand(Command.SET_TEMPO, Integer.parseInt(value)));
        break;
      }
    }
  }

  @Override
  protected void doLoad() {
    addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "body", karaokeView.render()));
    karaokeView.selectSong(karaokeView.getSongIndex()); // refresh highlight on current song
  }

  private void backToSongs() {
    karaokeView.selectSongsTab();
    sampleSong(karaokeView.getSongIndex());
  }

  private void doCommand(OnCommand message) {
    switch (message.getCommand()) {
    case SET_BACKGROUND_VELOCITY:
      doSetBackgroundVelocity(message.getParameter());
      break;
    case SET_MASTER_GAIN:
      doSetMasterGain(message.getParameter());
      break;
    case SET_TEMPO:
      doSetTempo(message.getParameter());
      break;
    default:
      break;

    }
  }

  private void doDeviceCommand(OnDeviceCommand message) {
    switch (message.getDeviceCommand()) {
    case MUTE_BACKGROUND:
      doSetBackgroundMute(message.getDeviceIndex(), message.getParameter());
      break;
    case PROGRAM:
      doProgram(message.getDeviceIndex(), message.getParameter());
      break;
    case VELOCITY:
      doSetDeviceVelocity(message.getDeviceIndex(), message.getParameter());
      break;
    default:
      break;
    }
  }

  private void doPickChannel(OnPickChannel message) {
    karaokeView.renderChannelList(message.getSong(), message.getDeviceIndex(), message.getDeviceChannelAssignments());
    sampleChannel(karaokeView.getChannelIndex());
  }

  private void doProgram(int deviceIndex, int program) {
    karaokeView.updateProgram(deviceIndex, program);
  }

  private void doRenderSong(OnRenderSong message) {
    NavigableMap<Integer, PlayerDetail> devicePlayerDetail = new TreeMap<>();
    NavigableMap<Integer, Integer> deviceChannelAssignments = message.getDeviceChannelAssignments();
    for (Entry<Integer, Integer> entry : deviceChannelAssignments.entrySet()) {
      Integer deviceIndex = entry.getKey();
      PlayerDetail playerDetail = request(new PlayerDetailService(deviceIndex));
      devicePlayerDetail.put(deviceIndex, playerDetail);
    }
    karaokeView.renderSong(message.getSong(), devicePlayerDetail);
    for (Integer deviceIndex : deviceChannelAssignments.keySet()) {
      karaokeView.setDeviceVelocity(deviceIndex, request(new PlayerVelocityService(deviceIndex)));
      karaokeView.setBackgroundMute(deviceIndex, request(new BackgroundMuteService(deviceIndex)));
    }
    karaokeView.setBackgroundVelocity(request(Services.getBackgroundVelocity));
    karaokeView.setMasterGain(request(Services.getMasterGain));
    karaokeView.setTempo(request(Services.getTempo));
  }

  private void doSampleChannel(OnSampleChannel message) {
    karaokeView.selectChannel(message.getSong(), message.getChannel());
  }

  private void doSampleSong(OnSampleSong message) {
    karaokeView.renderSongDetails(message.getSong());
    karaokeView.selectSong(message.getSongIndex());
    karaokeView.selectSongsTab();
  }

  private void doSetBackgroundMute(int deviceIndex, int mute) {
    karaokeView.setBackgroundMute(deviceIndex, mute);
  }

  private void doSetBackgroundVelocity(int velocity) {
    karaokeView.setBackgroundVelocity(velocity);
  }

  private void doSetDeviceVelocity(int deviceIndex, int velocity) {
    karaokeView.setDeviceVelocity(deviceIndex, velocity);
  }

  private void doSetMasterGain(int gain) {
    karaokeView.setMasterGain(gain);
  }

  private void doSetTempo(int tempo) {
    karaokeView.setTempo(tempo);
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
    RandomAccessList<File> midiFiles = request(Services.getMidiFiles);
    pickRandomSong(midiFiles);
  }

  private void pickRandomSong(RandomAccessList<File> midiFiles) {
    if (midiFiles.size() > 0) {
      int songIndex = random.nextInt(midiFiles.size());
      sampleSong(songIndex);
    }
  }

  private void play() {
    publish(new OnCommand(Command.PLAY, ChannelNotes.ALL_CHANNELS));
  }

  private void sampleChannel(int channelIndex) {
    publish(new OnDeviceCommand(DeviceCommand.SAMPLE_CHANNEL, karaokeView.getDeviceIndex(), channelIndex));
  }

  private void sampleSong(int songIndex) {
    karaokeView.selectSong(songIndex);
    publish(new OnCommand(Command.SAMPLE_SONG, songIndex));
  }

  private void seek(int tick) {
    publish(new OnCommand(Command.SEEK, tick));
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
