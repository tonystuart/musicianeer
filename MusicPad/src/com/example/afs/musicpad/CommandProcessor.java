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

import com.example.afs.musicmaker.analyzer.Analyzer;
import com.example.afs.musicpad.message.Command;
import com.example.afs.musicpad.song.MusicLibrary;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.SongBuilder;
import com.example.afs.musicpad.util.MessageBroker;
import com.example.afs.musicpad.util.Task;

// new things
// zero/enter when not in command mode are modulation down/up1550000550
// contour
// save current recording
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

public class CommandProcessor extends Task {

  private MusicLibrary musicLibrary;

  protected CommandProcessor(MessageBroker messageBroker, MusicLibrary musicLibrary) {
    super(messageBroker);
    this.musicLibrary = musicLibrary;
    subscribe(Command.class, message -> onCommand(message.getCommand(), message.getOperand()));
  }

  private void onCommand(int command, int operand) {
    System.out.println("CommandProcessor.onCommand: command=" + command + ", operand=" + operand);
    switch (command) {
    case 1:
      selectSong(operand);
      break;
    }
  }

  private void selectSong(int songIndex) {
    if (songIndex < musicLibrary.size()) {
      File midiFile = musicLibrary.getMidiFile(songIndex);
      SongBuilder songBuilder = new SongBuilder();
      Song song = songBuilder.createSong(midiFile);
      System.out.println("Selecting song #" + songIndex + ": " + song.getName());
      Analyzer.displaySemitoneCounts(song);
      Analyzer.displayKey(song);
    } else {
      System.out.println("Song " + songIndex + " is out of range");
    }
  }

}
