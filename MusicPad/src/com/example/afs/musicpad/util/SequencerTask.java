// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class SequencerTask<T> extends BrokerTask<T> {

  private Scheduler<T> scheduler;

  public SequencerTask(Scheduler<T> scheduler, Broker<T> broker) {
    this(scheduler, broker, 0);
  }

  public SequencerTask(Scheduler<T> scheduler, Broker<T> broker, long timeoutMillis) {
    super(broker, timeoutMillis);
    this.scheduler = scheduler;
  }

  @Override
  protected BlockingQueue<T> createInputQueue() {
    return new PriorityBlockingQueue<>();
  }

  @Override
  protected void processMessage(T message) throws InterruptedException {
    long currentTimestamp = System.currentTimeMillis();
    long eventTimestamp = scheduler.getEventTimeMillis(message);
    if (eventTimestamp > currentTimestamp) {
      long sleepInterval = eventTimestamp - currentTimestamp;
      Thread.sleep(sleepInterval);
    }
    super.processMessage(message);
  }

}
