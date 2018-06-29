// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.util;

public class DelayTimer {

  public class TimerThread extends Thread {
    @Override
    public void run() {
      long sleepInterval;
      while ((sleepInterval = getRemainingSleepInterval()) > 0) {
        try {
          setName(DelayTimer.class.getSimpleName() + " - " + sleepInterval + " ms");
          sleep(sleepInterval);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private long timeout;
  private Thread thread;
  private Runnable runnable;

  public DelayTimer(Runnable runnable) {
    this.runnable = runnable;
  }

  public synchronized void delay(int millis) {
    timeout = System.currentTimeMillis() + millis;
    if (thread == null) {
      thread = new TimerThread();
      thread.start();
    }
  }

  private synchronized long getRemainingSleepInterval() {
    long sleepInterval = timeout - System.currentTimeMillis();
    if (sleepInterval <= 0) {
      thread = null;
      runnable.run();
    }
    return sleepInterval;
  }

}
