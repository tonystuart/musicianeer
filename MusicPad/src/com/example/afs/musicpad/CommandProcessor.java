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
import java.util.Random;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.analyzer.Analyzer;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnPlay;
import com.example.afs.musicpad.message.OnStop;
import com.example.afs.musicpad.message.OnTempo;
import com.example.afs.musicpad.message.OnVolume;
import com.example.afs.musicpad.message.OnSongSelected;
import com.example.afs.musicpad.parser.SongBuilder;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;

// new things
// zero/enter when not in command mode are modulation down/up1550000550
// contour// save current recording
// clear current recording
// next four measures
// previous four measures
// control
// 1 - play
// 2 - pause/stop
// 3 - stop
// 4 - previous measure
// 5 - next measure
// 6 - loop
// 7 - record
// 8 - previous song
// 9 - next song
// 10 - new clip
// 11 - previous clip
// 12 - next clip
// 13 - transpose to white
// 20 - clip append
// 21 - clip merge
// control, value
// 100 - set song
// 101 - set song page
// 102 - set song index on page
// 103 - set master gain
// 104 - set tempo
// 105 - set note concurrency
// 106 - set note duration
// 107 - set default program
// 108 - set default velocity
// 109 - set loop start (in measures)
// 110 - set loop length (in beats)
// control-range
// 200 to 299 - play next note in track
// 300 to 399 - play note in page
// 400 to 499 - set clip
// 500 to 599 - play clip
// control-range, value
// 600 to 699 - set track program
// 700 to 799 - set track velocity

public class CommandProcessor extends BrokerTask<Message> {

  private static final int PAGE_SIZE = 10;

  private Song currentSong;
  private MusicLibrary musicLibrary;
  private Random random = new Random();

  protected CommandProcessor(Broker<Message> broker, Synthesizer synthesizer, MusicLibrary musicLibrary) {
    super(broker);
    this.musicLibrary = musicLibrary;
    subscribe(OnCommand.class, message -> onCommand(message.getCommand(), message.getParameter()));
  }

  private void doListSongs(int pageNumber) {
    int base;
    int limit;
    if (pageNumber == 0) {
      base = 0;
      limit = musicLibrary.size();
    } else {
      int pagesAvailable = musicLibrary.size() / PAGE_SIZE;
      int pageIndex = Math.min(pageNumber - 1, pagesAvailable);
      base = pageIndex * PAGE_SIZE;
      limit = Math.min(base + PAGE_SIZE, musicLibrary.size());
      System.out.println("Page #" + (pageIndex + 1));
    }
    for (int songIndex = base; songIndex < limit; songIndex++) {
      int songNumber = songIndex + 1;
      File midiFile = musicLibrary.getMidiFile(songIndex);
      System.out.println("Song #" + songNumber + ": " + midiFile.getName());
    }
  }

  private void doPlay(int channelNumber) {
    int channelIndex = channelNumber - 1;
    if (currentSong != null) {
      getBroker().publish(new OnPlay(currentSong, channelIndex));
    }
  }

  private void doSelectSong(int songNumber) {
    int songIndex;
    if (songNumber == 0) {
      songIndex = random.nextInt(musicLibrary.size());
      songNumber = songIndex + 1;
    } else {
      songIndex = songNumber - 1;
    }
    if (songIndex < musicLibrary.size()) {
      File midiFile = musicLibrary.getMidiFile(songIndex);
      SongBuilder songBuilder = new SongBuilder();
      currentSong = songBuilder.createSong(midiFile);
      System.out.println("Selecting #" + songNumber + ": " + currentSong.getName());
      Analyzer.displaySemitoneCounts(currentSong);
      Analyzer.displayKey(currentSong);
      Analyzer.displayDrumCounts(currentSong);
      publish(new OnSongSelected(currentSong));
    } else {
      System.out.println("Song " + songNumber + " is out of range");
    }
  }

  private void doSetPercentTempo(int percentTempo) {
    getBroker().publish(new OnTempo(percentTempo));
  }

  private void doSetPercentVolume(int percentVolume) {
    getBroker().publish(new OnVolume(percentVolume));
  }

  private void doStop(int parameter) {
    getBroker().publish(new OnStop());
  }

  private void onCommand(int command, int parameter) {
    switch (command) {
    case Command.SELECT_SONG:
      doSelectSong(parameter);
      break;
    case Command.LIST_SONGS:
      doListSongs(parameter);
      break;
    case Command.PLAY:
      doPlay(parameter);
      break;
    case Command.STOP:
      doStop(parameter);
      break;
    case Command.SET_PERCENT_TEMPO:
      doSetPercentTempo(parameter);
      break;
    case Command.SET_PERCENT_VOLUME:
      doSetPercentVolume(parameter);
      break;
    default:
      System.out.println("CommandProcessor.onCommand: command=" + command + ", parameter=" + parameter);
    }
  }

}
