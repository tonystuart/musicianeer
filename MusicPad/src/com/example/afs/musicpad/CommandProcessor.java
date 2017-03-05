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
import com.example.afs.musicpad.message.Command;
import com.example.afs.musicpad.message.CommandForwarded;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.SongSelected;
import com.example.afs.musicpad.parser.SongBuilder;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.transport.Transport;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.BrokerTask;

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

  private Song currentSong;
  private int currentPageIndex = -1;
  private MusicLibrary musicLibrary;
  private Random random = new Random();
  private Transport transport;

  protected CommandProcessor(Broker<Message> messageBroker, Synthesizer synthesizer, MusicLibrary musicLibrary) {
    super(messageBroker);
    this.musicLibrary = musicLibrary;
    this.transport = new Transport(synthesizer);
    subscribe(CommandForwarded.class, message -> onCommand(message.getCommand(), message.getParameter()));
  }

  private void doListSongs(int pageNumber) {
    int pageIndex;
    if (pageNumber == 0) {
      pageIndex = currentPageIndex + 1;
      pageNumber = pageIndex + 1;
    } else {
      pageIndex = pageNumber - 1;
    }
    System.out.println("Page #" + pageNumber);
    int base = pageIndex * 100;
    int limit = Math.min(base + 100, musicLibrary.size());
    for (int songIndex = base; songIndex < limit; songIndex++) {
      int songNumber = songIndex + 1;
      File midiFile = musicLibrary.getMidiFile(songIndex);
      System.out.println("Song #" + songNumber + ": " + midiFile.getName());
    }
    currentPageIndex = pageIndex;
  }

  private void doPlay(int channelNumber) {
    int channelIndex = channelNumber - 1;
    transport.stop();
    if (currentSong != null) {
      transport.play(currentSong, channelIndex);
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
      publish(new SongSelected(currentSong));
    } else {
      System.out.println("Song " + songNumber + " is out of range");
    }
  }

  private void doStop(int parameter) {
    transport.stop();
  }

  private void onCommand(int command, int parameter) {
    System.out.println("CommandProcessor.onCommand: command=" + command + ", parameter=" + parameter);
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
    }
  }

}
