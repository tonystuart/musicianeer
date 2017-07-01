// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad;

import java.io.File;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnAllTasksStarted;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnMidiFiles;
import com.example.afs.musicpad.message.OnRepublishState;
import com.example.afs.musicpad.message.OnSample;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.parser.SongBuilder;
import com.example.afs.musicpad.song.ChannelNotes;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Conductor extends BrokerTask<Message> {

  private static final int TICKS_PER_PIXEL = 5;

  private Song song;
  private File directory;
  private RandomAccessList<File> midiFiles;

  public Conductor(Broker<Message> broker, String path) {
    super(broker);
    this.directory = new File(path);
    this.midiFiles = new DirectList<>();
    listMidiFiles(midiFiles, directory);
    midiFiles.sort((o1, o2) -> o1.getPath().compareTo(o2.getPath()));
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnAllTasksStarted.class, message -> doAllTasksStarted());
    subscribe(OnRepublishState.class, message -> doRepublishState());
  }

  private void doAllTasksStarted() {
    System.out.println("Conductor.doAllTasksStarted: deferring initialization until OnRepublishState");
    //publish(new OnMidiFiles(midiFiles));
  }

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    int parameter = message.getParameter();
    switch (command) {
    case SAMPLE:
      doSample(parameter);
      break;
    case SAMPLE_CHANNEL:
      doSampleChannel(parameter);
      break;
    case SONG:
      doSelectSong(parameter);
      break;
    case TRANSPOSE:
      doTranspose(parameter);
      break;
    default:
      break;
    }
  }

  private void doRepublishState() {
    publish(new OnMidiFiles(midiFiles));
  }

  private void doSample(int songIndex) {
    if (songIndex >= 0 && songIndex < midiFiles.size()) {
      File midiFile = midiFiles.get(songIndex);
      SongBuilder songBuilder = new SongBuilder();
      song = songBuilder.createSong(midiFile);
      System.out.println("Sampling song " + songIndex + " - " + song.getTitle());
      publish(new OnSample(song, ChannelNotes.ALL_CHANNELS, TICKS_PER_PIXEL));
    }
  }

  private void doSampleChannel(int channel) {
    if (song != null) {
      System.out.println("Sampling channel " + channel);
      publish(new OnSample(song, channel, TICKS_PER_PIXEL));
    }
  }

  private void doSelectSong(int songIndex) {
    if (songIndex >= 0 && songIndex < midiFiles.size()) {
      File midiFile = midiFiles.get(songIndex);
      SongBuilder songBuilder = new SongBuilder();
      song = songBuilder.createSong(midiFile);
      System.out.println("Selecting song " + songIndex + " - " + song.getTitle());
      publish(new OnSong(song, TICKS_PER_PIXEL));
    }
  }

  private void doTranspose(int distance) {
    song.transposeTo(distance);
    publish(new OnSong(song, TICKS_PER_PIXEL));
  }

  private boolean isMidiFile(String name) {
    String lowerCaseName = name.toLowerCase();
    boolean isMidi = lowerCaseName.endsWith(".mid") || lowerCaseName.endsWith(".kar");
    return isMidi;
  }

  private void listMidiFiles(RandomAccessList<File> midiFiles, File parent) {
    if (!parent.isDirectory() || !parent.canRead()) {
      throw new IllegalArgumentException(parent + " is not a readable directory");
    }
    File[] files = parent.listFiles((dir, name) -> isMidiFile(name));
    for (File file : files) {
      if (file.isFile()) {
        midiFiles.add(file);
      } else if (file.isDirectory()) {
        listMidiFiles(midiFiles, file);
      }
    }
  }

}