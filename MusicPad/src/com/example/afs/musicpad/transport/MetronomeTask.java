// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnPlay;
import com.example.afs.musicpad.message.OnStop;
import com.example.afs.musicpad.message.OnTempo;
import com.example.afs.musicpad.message.TickOccurred;
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
    subscribe(OnStop.class, message -> onStop());
    subscribe(OnPlay.class, message -> onPlay(message.getSong(), message.getChannel()));
    subscribe(OnTempo.class, message -> onTempo(message.getPercentTempo()));
  }

  @Override
  protected void onTimeout() throws InterruptedException {
    getBroker().publish(new TickOccurred(tick));
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

  private void onPlay(Song song, int channel) {
    this.tick = 0;
    this.song = song;
    tickScheduler.reset();
    calculateTimeout();
  }

  private void onStop() {
    setTimeoutMillis(0);
  }

  private void onTempo(int percentTempo) {
    tickScheduler.setPercentTempo(percentTempo);
  }
}
