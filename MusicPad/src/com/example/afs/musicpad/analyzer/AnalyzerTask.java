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
import com.example.afs.musicpad.device.midi.configuration.ChannelState;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Table;
import com.example.afs.musicpad.html.TableHead;
import com.example.afs.musicpad.html.TableRow;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelState;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDetails;
import com.example.afs.musicpad.message.OnSong;
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
    subscribe(OnSong.class, message -> doSong(message.getSong()));
  }

  private void doCommand(Command command, int parameter) {
    switch (command) {
    case SHOW_CHANNEL_STATE:
      doShowChannelState();
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

  private void doShowChannelState() {
    if (currentSong != null) {
      showChannelState(currentSong);
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

  private void doSong(Song song) {
    currentSong = song;
    showChannelInfo(currentSong);
  }

  private String getDuration() {
    long tickDuration = currentSong.getDuration();
    int beatsPerMinute = currentSong.getBeatsPerMinute(0);
    int beatsPerSecond = beatsPerMinute * 60;
    long secondsDuration = tickDuration / beatsPerSecond;
    String duration = String.format("%d:%02d", secondsDuration / 60, secondsDuration % 60);
    return duration;
  }

  private void showChannelInfo(Song song) {
    Division detail = new Division();
    detail.setId("detail");

    Table songTable = new Table();
    TableHead songHeader = songTable.createHead();

    songHeader.append("Title");
    songHeader.append("BPM");
    songHeader.append("Tempo");
    songHeader.append("Duration");

    TableRow songRow = songTable.createRow();
    songRow.append(song.getName());
    songRow.append(song.getBeatsPerMinute(0));
    songRow.append(song.getBeatsPerMeasure(0) + "/" + song.getBeatUnit(0));
    songRow.append(getDuration());

    detail.append(songTable);

    Table channelTable = new Table();
    TableHead channelHeader = channelTable.createHead();

    channelHeader.append("Channel");
    channelHeader.append("Total Notes");
    channelHeader.append("Lowest Note");
    channelHeader.append("Highest Note");
    channelHeader.append("Occupancy");
    channelHeader.append("Concurrency");

    for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
      channelHeader.append(Names.getNoteName(semitone));
    }

    channelHeader.append("Instruments");

    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      int noteCount = song.getChannelNoteCount(channel);
      if (noteCount > 0) {
        if (channel != Midi.DRUM) {
          TableRow channelRow = new TableRow();
          int occupancy = song.getOccupancy(channel);
          int concurrency = song.getConcurrency(channel);
          channelRow.append(Value.toNumber(channel));
          channelRow.append(noteCount);
          channelRow.append(song.getLowestMidiNote(channel));
          channelRow.append(song.getHighestMidiNote(channel));
          channelRow.append(occupancy);
          channelRow.append(concurrency);
          for (int semitone = 0; semitone < Midi.SEMITONES_PER_OCTAVE; semitone++) {
            int commonNoteCount = song.getCommonNoteCounts(channel)[semitone];
            channelRow.append(commonNoteCount);
          }
          channelRow.append(song.getProgramNames(channel));

          channelTable.append(channelRow);
        }
      }
    }

    detail.append(channelTable);

    StringBuilder s = new StringBuilder();
    detail.render(s);
    publish(new OnDetails(s.toString()));
  }

  private void showChannelState(Song song) {
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      int noteCount = song.getChannelNoteCount(channel);
      publish(new OnChannelState(channel, noteCount == 0 ? ChannelState.INACTIVE : ChannelState.ACTIVE));
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
