// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class SimpleTask<M> {

  public static int NO_TIMEOUT = 0;

  private long timeoutMillis;
  private boolean isTerminated;
  private String name;
  private Thread thread;
  private BlockingQueue<M> inputQueue = createInputQueue();

  protected SimpleTask() {
    this(NO_TIMEOUT);
  }

  protected SimpleTask(long timeoutMillis) {
    this.timeoutMillis = timeoutMillis;
    this.name = getClass().getSimpleName();
  }

  public BlockingQueue<M> tsGetInputQueue() {
    return inputQueue;
  }

  public synchronized void tsStart() {
    if (thread == null) {
      thread = new Thread(() -> run(), name);
      thread.start();
    }
  }

  public synchronized void tsTerminate() {
    if (thread == null) {
      throw new IllegalStateException();
    }
    if (!isTerminated) {
      isTerminated = true;
      thread.interrupt();
    }
  }

  protected BlockingQueue<M> createInputQueue() {
    return new LinkedBlockingQueue<>();
  }

  protected boolean isTerminated() {
    return isTerminated;
  }

  protected void onTimeout() throws InterruptedException {
  }

  protected void processMessage(M message) throws InterruptedException {
  }

  protected void run() {
    while (!isTerminated) {
      try {
        if (timeoutMillis == NO_TIMEOUT) {
          M message = inputQueue.take();
          processMessage(message);
        } else {
          M message = inputQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
          if (message == null) {
            onTimeout();
          } else {
            processMessage(message);
          }
        }
      } catch (RuntimeException e) {
        e.printStackTrace();
        System.err.println("SimpleTask: ignoring exception in " + name);
      } catch (InterruptedException e) {
        isTerminated = true;
      }
    }
    System.out.println("SimpleTask: returning from run method in " + name);
  }

  protected void setTimeoutMillis(long timeoutMillis) {
    this.timeoutMillis = timeoutMillis;
  }

}
