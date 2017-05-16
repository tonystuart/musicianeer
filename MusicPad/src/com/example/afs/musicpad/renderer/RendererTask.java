// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.device.midi.configuration.ChannelState;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelState;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnFooter;
import com.example.afs.musicpad.message.OnHeader;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.message.OnTransport;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;

public class RendererTask extends BrokerTask<Message> {

  private Song currentSong;

  public RendererTask(Broker<Message> broker) {
    super(broker);
    subscribe(OnCommand.class, message -> doCommand(message.getCommand(), message.getParameter()));
    subscribe(OnSong.class, message -> doSong(message.getSong()));
  }

  private void doCommand(Command command, int parameter) {
    switch (command) {
    case SHOW_CHANNEL_STATE:
      doShowChannelState();
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

  private void doSong(Song song) {
    currentSong = song;
    publish(new OnHeader(new HeaderRenderer(song).render()));
    publish(new OnFooter(new FooterRenderer(song).render()));
    publish(new OnTransport(new TransportRenderer(song).render()));
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
}
