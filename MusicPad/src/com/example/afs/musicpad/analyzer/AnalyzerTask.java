// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.device.midi.MidiConfiguration.ChannelState;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelState;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnSongSelected;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Value;

public class AnalyzerTask extends BrokerTask<Message> {

  private Song currentSong;

  public AnalyzerTask(Broker<Message> broker) {
    super(broker);
    subscribe(OnCommand.class, message -> doCommand(message.getCommand(), message.getParameter()));
    subscribe(OnSongSelected.class, message -> doSongSelected(message.getSong()));
  }

  private void doCommand(Command command, int parameter) {
    switch (command) {
    case SHOW_CHANNEL_INFO:
      doShowChannelInfo();
      break;
    case SHOW_KEY_INFO:
      doShowKeyInfo();
      break;
    case SHOW_DRUM_INFO:
      doShowDrumInfo();
      break;
    default:
      break;
    }
  }

  private void doShowChannelInfo() {
    if (currentSong != null) {
      showChannelInfo(currentSong);
    }
  }

  private void doShowDrumInfo() {
    if (currentSong != null) {
      showDrumInfo(currentSong);
    }
  }

  private void doShowKeyInfo() {
    if (currentSong != null) {
      showKeyInfo(currentSong);
    }
  }

  private void doSongSelected(Song song) {
    currentSong = song;
    showChannelInfo(currentSong);
  }

  private void showChannelInfo(Song song) {
    System.out.print("CHN   TOT OCC CON");
    for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
      System.out.printf(" %3s", Names.getNoteName(semitone));
    }
    System.out.println();
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      int noteCount = song.getChannelNoteCount(channel);
      publish(new OnChannelState(channel, noteCount == 0 ? ChannelState.INACTIVE : ChannelState.ACTIVE));
      if (noteCount > 0) {
        if (channel != Midi.DRUM) {
          int occupancy = song.getOccupancy(channel);
          int concurrency = song.getConcurrency(channel);
          System.out.printf("%3d %5d %3d %3d", Value.toNumber(channel), noteCount, occupancy, concurrency);
          for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
            int commonNoteCount = song.getCommonNoteCounts(channel)[semitone];
            System.out.printf(" %3d", commonNoteCount);
          }
          System.out.println(" " + song.getProgramNames(channel));
        }
      }
    }
  }

  private void showDrumInfo(Song song) {
    int drumBeatCount = song.getChannelNoteCount(Midi.DRUM);
    if (drumBeatCount > 0) {
      System.out.println("CHN 9 TOT " + drumBeatCount);
      int[] distinctNoteCount = song.getDistinctNoteCount(Midi.DRUM);
      for (int drum = 0; drum < Midi.NOTES; drum++) {
        int count = distinctNoteCount[drum];
        if (count > 0) {
          System.out.printf("%4d [%s]\n", count, Instruments.getDrumName(drum));
        }
      }
    }
  }

  private void showKeyInfo(Song song) {
    System.out.println("CHN RNK KEY      SYNOPSIS ACCIDENTALS TRIADS THIRDS");
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (song.getChannelNoteCount(channel) > 0) {
        if (channel != Midi.DRUM) {
          int[] commonNoteCounts = song.getCommonNoteCounts(channel);
          KeyScore[] keyScores = KeySignatures.getKeyScores(commonNoteCounts);
          for (KeyScore keyScore : keyScores) {
            int rank = keyScore.getRank();
            if (rank == 1) {
              String key = keyScore.getKey();
              String synopsis = keyScore.getSynopsis();
              int accidentals = keyScore.getAccidentals();
              int triads = keyScore.getTriads();
              int thirds = keyScore.getThirds();
              System.out.printf("%3d %3d %-8s %-8s         %3d    %3d    %3d\n", Value.toNumber(channel), rank, key, synopsis, accidentals, triads, thirds);
            }
          }
        }
      }
    }
  }
}
