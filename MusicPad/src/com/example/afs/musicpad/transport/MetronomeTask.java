// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;

public class MetronomeTask extends BrokerTask<Message> {

  private long tick;
  private Song song;
  private TickScheduler tickScheduler = new TickScheduler();

  public MetronomeTask(Broker<Message> broker) {
    super(broker);
    subscribe(OnSong.class, message -> doSongSelected(message.getSong()));
    subscribe(OnCommand.class, message -> doCommand(message.getCommand(), message.getParameter()));
  }

  @Override
  protected void onTimeout() throws InterruptedException {
    getBroker().publish(new OnTick(tick));
    tick += Default.TICKS_PER_BEAT;
    calculateTimeout();
  }

  private void calculateTimeout() {
    int beatsPerMinute = song.getBeatsPerMinute(tick);
    long eventTimeMillis = tickScheduler.getEventTimeMillis(tick, beatsPerMinute);
    long deltaTimeMillis = eventTimeMillis - System.currentTimeMillis();
    if (deltaTimeMillis < 1) {
      deltaTimeMillis = 1;
    }
    System.out.println("deltaTimeMillis=" + deltaTimeMillis);
    setTimeoutMillis(deltaTimeMillis);
  }

  private void doCommand(Command command, int parameter) {
    switch (command) {
    case PLAY:
      play(parameter);
      break;
    case STOP:
      stop();
      break;
    case SET_TRANSPORT_TEMPO:
      setPercentTempo(parameter);
      break;
    default:
      break;
    }
  }

  private void doSongSelected(Song song) {
    stop();
    this.song = song;
  }

  private void play(int channelNumber) {
    if (song != null) {
      this.tick = 0;
      tickScheduler.resetAll();
      calculateTimeout();
    }
  }

  private void setPercentTempo(int percentTempo) {
    tickScheduler.setPercentTempo(percentTempo);
  }

  private void stop() {
    setTimeoutMillis(0);
  }
}
