// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.example.afs.musicpad.util.MessageBroker.Message;
import com.example.afs.musicpad.util.MessageBroker.Subscriber;

public abstract class Task {

  private MessageBroker messageBroker;
  private long timeoutMillis;

  private Thread thread;
  private BlockingQueue<Message> inputQueue = new LinkedBlockingQueue<>();
  private Map<Class<? extends Message>, Subscriber<? extends Message>> subscribers = new HashMap<>();
  private boolean isTerminated;

  protected Task(MessageBroker messageBroker) {
    this.messageBroker = messageBroker;
  }

  protected Task(MessageBroker messageBroker, long timeoutMillis) {
    this.messageBroker = messageBroker;
    this.timeoutMillis = timeoutMillis;
  }

  public void start() {
    thread = new Thread(() -> run(), getClass().getSimpleName());
    thread.start();
  }

  public void terminate() {
    if (thread == null) {
      throw new IllegalStateException();
    }
    thread.interrupt();
  }

  protected MessageBroker getMessageBroker() {
    return messageBroker;
  }

  protected boolean isTerminated() {
    return isTerminated;
  }

  protected void onTimeout() {
  }

  protected <T extends Message> void subscribe(Class<T> type, Subscriber<T> subscriber) {
    messageBroker.subscribe(type, message -> inputQueue.add(message));
    subscribers.put(type, subscriber);
  }

  private void processMessage(Message message) {
    @SuppressWarnings("unchecked")
    Subscriber<Message> subscriber = (Subscriber<Message>) subscribers.get(message.getClass());
    subscriber.onMessage(message);
  }

  private void run() {
    while (!isTerminated) {
      try {
        if (timeoutMillis == 0) {
          Message message = inputQueue.take();
          processMessage(message);
        } else {
          Message message = inputQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
          if (message == null) {
            onTimeout();
          } else {
            processMessage(message);
          }
        }
      } catch (RuntimeException e) {
        e.printStackTrace();
        System.err.println("Task.run: ignoring exception");
      } catch (InterruptedException e) {
        System.out.println("Task.run: terminating");
        isTerminated = true;
      }
    }
  }

}
