// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.task;

import com.example.afs.musicpad.util.Broker;

public class PausibleSequencerTask<T> extends SequencerTask<T> {

  private boolean isPaused;

  public PausibleSequencerTask(Scheduler<T> scheduler, Broker<T> broker) {
    super(scheduler, broker, 0);
  }

  public PausibleSequencerTask(Scheduler<T> scheduler, Broker<T> broker, long timeoutMillis) {
    super(scheduler, broker, timeoutMillis);
  }

  public boolean isPaused() {
    return isPaused;
  }

  public void setPaused(boolean isPaused) {
    this.isPaused = isPaused;
  }

  @Override
  protected void processMessage(T message) throws InterruptedException {
    super.processMessage(message);
    while (isPaused && !isTerminated()) {
      Thread.sleep(250);
    }
  }

}
