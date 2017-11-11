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
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceAttached;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnDeviceDetached;
import com.example.afs.musicpad.message.OnPickChannel;
import com.example.afs.musicpad.message.OnRenderSong;
import com.example.afs.musicpad.message.OnSampleChannel;
import com.example.afs.musicpad.message.OnSampleSong;
import com.example.afs.musicpad.parser.SongBuilder;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Conductor extends ServiceTask {

  private File directory;
  private Song song;
  private RandomAccessList<File> midiFiles;
  private NavigableSet<Integer> deviceIndexes = new TreeSet<>();
  private NavigableMap<Integer, Integer> deviceChannelAssignments = new TreeMap<>();

  public Conductor(MessageBroker broker, String path) {
    super(broker);
    this.directory = new File(path);
    this.midiFiles = new DirectList<>();
    listMidiFiles(midiFiles, directory);
    midiFiles.sort((o1, o2) -> o1.getPath().compareTo(o2.getPath()));
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnDeviceCommand.class, message -> doDeviceCommand(message));
    subscribe(OnDeviceAttached.class, message -> doDeviceAttached(message));
    subscribe(OnDeviceDetached.class, message -> doDeviceDetached(message));
    // TODO: Subscribe to OnChannelUpdate and publish so that things like KaraokeController can render a new prompter
    provide(Services.GetMidiFiles, () -> midiFiles);
    provide(Services.GetCurrentSong, () -> song);
  }

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    int parameter = message.getParameter();
    switch (command) {
    case SAMPLE_SONG:
      doSampleSong(parameter);
      break;
    case SELECT_SONG:
      doSelectSong(parameter);
      break;
    case TRANSPOSE:
      doTranspose(parameter);
      break;
    default:
      break;
    }
  }

  private void doDeviceAttached(OnDeviceAttached message) {
    int deviceIndex = message.getDeviceIndex();
    deviceIndexes.add(deviceIndex);
  }

  private void doDeviceCommand(OnDeviceCommand message) {
    int deviceIndex = message.getDeviceIndex();
    int parameter = message.getParameter();
    switch (message.getDeviceCommand()) {
    case INPUT:
      doInput(deviceIndex, parameter);
      break;
    case SAMPLE_CHANNEL:
      doSampleChannel(deviceIndex, parameter);
      break;
    case SELECT_CHANNEL:
      doSelectChannel(deviceIndex, parameter);
      break;
    default:
      break;
    }
  }

  private void doDeviceDetached(OnDeviceDetached message) {
    Integer deviceIndex = message.getDeviceIndex();
    deviceIndexes.remove(deviceIndex);
    deviceChannelAssignments.remove(deviceIndex);
    if (deviceIndexes.size() == deviceChannelAssignments.size()) {
      publish(new OnRenderSong(song, deviceChannelAssignments));
    } else {
      Integer next;
      if (deviceChannelAssignments.size() > 0) {
        next = deviceIndexes.higher(deviceChannelAssignments.lastKey());
      } else {
        next = deviceIndexes.first();
      }
      publish(new OnPickChannel(song, deviceChannelAssignments, next));
    }
  }

  private void doInput(int deviceIndex, int parameter) {
    if (deviceIndexes.size() == deviceChannelAssignments.size()) {
      publish(new OnRenderSong(song, deviceChannelAssignments));
    }
  }

  private void doSampleChannel(int deviceIndex, int channel) {
    System.out.println("Sampling channel " + channel);
    publish(new OnSampleChannel(song, deviceIndex, channel));
  }

  private void doSampleSong(int songIndex) {
    deviceChannelAssignments.clear();
    File midiFile = midiFiles.get(songIndex);
    SongBuilder songBuilder = new SongBuilder();
    song = songBuilder.createSong(midiFile);
    System.out.println("Sampling song " + songIndex + " - " + song.getTitle());
    publish(new OnSampleSong(song));
  }

  private void doSelectChannel(int deviceIndex, int channel) {
    deviceChannelAssignments.put(deviceIndex, channel);
    Integer next = deviceIndexes.higher(deviceIndex);
    if (next == null) {
      publish(new OnRenderSong(song, deviceChannelAssignments));
    } else {
      publish(new OnPickChannel(song, deviceChannelAssignments, next));
    }
  }

  private void doSelectSong(int songIndex) {
    System.out.println("Selecting song " + songIndex + " - " + song.getTitle());
    if (deviceIndexes.size() > 0) {
      publish(new OnPickChannel(song, deviceChannelAssignments, deviceIndexes.first()));
    }
  }

  private void doTranspose(int distance) {
    song.transposeTo(distance);
    publish(new OnSampleSong(song));
  }

  private boolean isMidiFile(String name) {
    String lowerCaseName = name.toLowerCase();
    boolean isMidi = lowerCaseName.endsWith(".mid") || lowerCaseName.endsWith(".midi") || lowerCaseName.endsWith(".kar");
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