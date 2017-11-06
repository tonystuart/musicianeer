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
import java.util.NavigableMap;
import java.util.Random;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.MidiFiles;
import com.example.afs.musicpad.html.Option;
import com.example.afs.musicpad.html.Template;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnKaraokeBandEvent;
import com.example.afs.musicpad.message.OnKaraokeBandHtml;
import com.example.afs.musicpad.message.OnKaraokeBandHtml.Action;
import com.example.afs.musicpad.message.OnSampleSong;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.playable.Playable;
import com.example.afs.musicpad.renderer.SongRenderer;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.util.RandomAccessList;

public class KaraokeRenderer extends SongRenderer {

  private KaraokeBand karaokeBand;
  private Random random = new Random();

  public KaraokeRenderer(MessageBroker broker) {
    super(broker);
    karaokeBand = new KaraokeBand(broker);
    subscribe(OnSampleSong.class, message -> doSampleSong(message));
    subscribe(OnKaraokeBandEvent.class, message -> doKaraokeBandEvent(message));
  }

  @Override
  public void start() {
    super.start();
    MidiFiles midiFilesResponse = request(MidiFiles.class);
    RandomAccessList<File> midiFiles = midiFilesResponse.getMidiFiles();
    karaokeBand.renderSongList(midiFiles);
    pickRandomSong(midiFiles);
  }

  @Override
  protected void render(Song song, NavigableMap<Integer, RandomAccessList<Playable>> devicePlayables, NavigableMap<Integer, Integer> deviceChannelAssignments) {
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
      case "roulette":
        pickRandomSong();
        break;
      case "stop":
        stop();
        break;
      case "select-song":
        selectSong();
        break;
      case "select-channel":
        selectChannel();
        break;
      case "back-to-songs":
        backToSongs();
        break;
      }
    }
  }

  private void doKaraokeBandEvent(OnKaraokeBandEvent message) {
    switch (message.getAction()) {
    case LOAD:
      doLoad();
      break;
    case CLICK:
      doClick(message.getId());
      break;
    default:
      break;
    }
  }

  private void doLoad() {
    publish(new OnKaraokeBandHtml(Action.REPLACE_CHILDREN, "body", karaokeBand.render()));
  }

  private void doSampleSong(OnSampleSong message) {
    karaokeBand.renderSongDetails(message.getSong());
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
  }

  private void sampleSong(int songIndex) {
    karaokeBand.selectSong(songIndex);
    publish(new OnCommand(Command.SAMPLE_SONG, songIndex));
  }

  private void selectChannel() {
  }

  private void selectSong() {
  }

  private void stop() {
  }

}
