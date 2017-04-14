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
import com.example.afs.musicpad.Trace.TraceOption;
import com.example.afs.musicpad.analyzer.TranspositionFinder;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnSongSelected;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.parser.SongBuilder;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Value;

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
  private static final float DEFAULT_GAIN = 5 * Synthesizer.DEFAULT_GAIN;

  private Song currentSong;
  private MusicLibrary musicLibrary;
  private Synthesizer synthesizer;
  private Random random = new Random();

  public CommandProcessor(Broker<Message> broker, Synthesizer synthesizer, MusicLibrary musicLibrary) {
    super(broker);
    this.synthesizer = synthesizer;
    this.musicLibrary = musicLibrary;
    subscribe(OnCommand.class, message -> doCommand(message.getCommand(), message.getParameter()));
    synthesizer.setGain(DEFAULT_GAIN);
  }

  private void doCommand(Command command, int parameter) {
    switch (command) {
    case HELP:
      doHelp();
      break;
    case SELECT_SONG:
      doSelectSong(parameter);
      break;
    case LIST_SONGS:
      doListSongs(parameter);
      break;
    case SET_MASTER_GAIN:
      doSetMasterGain(parameter);
      break;
    case TRANSPOSE:
      doTranspose();
      break;
    case TRON:
      doTron(parameter);
      break;
    case TROFF:
      doTroff(parameter);
      break;
    case QUIT:
      System.exit(0);
      break;
    default:
      break;
    }
  }

  private void doHelp() {
    for (Command command : Command.values()) {
      System.out.println(command.ordinal() + " -> " + command);
    }
  }

  private void doListSongs(int songNumber) {
    int base;
    int limit;
    if (songNumber == 0) {
      base = 0;
      limit = musicLibrary.size();
    } else {
      int songIndex = songNumber - 1;
      base = Math.max(0, songIndex);
      limit = Math.min(songIndex + PAGE_SIZE, musicLibrary.size());
    }
    for (int songIndex = base; songIndex < limit; songIndex++) {
      File midiFile = musicLibrary.getMidiFile(songIndex);
      System.out.println("Song #" + (songIndex + 1) + ": " + midiFile.getName());
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
      System.out.println("Selecting song " + songNumber + " - " + currentSong.getName());
      publish(new OnSongSelected(currentSong));
    } else {
      System.out.println("Song " + songNumber + " is out of range");
    }
  }

  private void doSetMasterGain(int masterGain) {
    float gain = Range.scale(Synthesizer.MINIMUM_GAIN, Synthesizer.MAXIMUM_GAIN, Midi.MIN_VALUE, Midi.MAX_VALUE, masterGain);
    synthesizer.setGain(gain);
  }

  private void doTranspose() {
    Song oldSong = currentSong;
    TranspositionFinder transpositionFinder = new TranspositionFinder();
    int distanceToWhiteKeys = transpositionFinder.getDistanceToWhiteKeys(oldSong);
    if (distanceToWhiteKeys == 0) {
      System.out.println("No transposition is required");
    } else {
      currentSong = currentSong.transpose(distanceToWhiteKeys);
      System.out.println("Selecting song " + currentSong.getName());
      publish(new OnSongSelected(currentSong));
    }
  }

  private void doTroff(int parameter) {
    setTrace(parameter, false);
  }

  private void doTron(int parameter) {
    setTrace(parameter, true);
  }

  private TraceOption getTraceOption(int traceNumber) {
    TraceOption traceOption;
    int trace = Value.toIndex(traceNumber);
    TraceOption[] traceOptions = TraceOption.values();
    if (trace < 0 || trace >= traceOptions.length) {
      traceOption = null;
      for (int i = 0; i < traceOptions.length; i++) {
        System.out.println((i + 1) + " -> " + traceOptions[i]);
      }
    } else {
      traceOption = traceOptions[trace];
    }
    return traceOption;
  }

  private void setTrace(int traceNumber, boolean value) {
    TraceOption traceOption = getTraceOption(traceNumber);
    if (traceOption != null) {
      switch (traceOption) {
      case COMMAND:
        Trace.setTraceCommand(value);
        break;
      case CONFIGURATION:
        Trace.setTraceConfiguration(value);
        break;
      case PLAY:
        Trace.setTracePlay(value);
        break;
      default:
        throw new UnsupportedOperationException();
      }
    }
  }

}
