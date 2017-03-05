// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Broker<B> {

  public interface Subscriber<M> {
    void onMessage(M message);
  }

  private Map<Object, Object> subscribers;

  public Broker() {
    subscribers = new ConcurrentHashMap<>();
  }

  public <T extends B> void publish(T message) {
    Queue<Subscriber<T>> queue = findQueue(message.getClass());
    if (queue != null) {
      for (Subscriber<T> subscriber : queue) {
        subscriber.onMessage(message);
      }
    }
  }

  public <T extends B> void subscribe(Class<T> type, Subscriber<T> subscriber) {
    synchronized (subscribers) {
      Queue<Subscriber<T>> queue = findQueue(type);
      if (queue == null) {
        queue = new ConcurrentLinkedQueue<Subscriber<T>>();
        subscribers.put(type, queue);
      }
      queue.add(subscriber);
    }
  }

  public <T extends B> void unsubscribe(Class<T> type, Subscriber<T> subscriber) {
    synchronized (subscribers) {
      Queue<Subscriber<T>> queue = findQueue(type);
      if (queue != null) {
        queue.remove(subscriber);
        if (queue.size() == 0) {
          subscribers.remove(type);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends B> Queue<Subscriber<T>> findQueue(Class<? extends Object> type) {
    Class<?> classType = type;
    Queue<Subscriber<T>> queue;
    while ((queue = (Queue<Subscriber<T>>) subscribers.get(classType)) == null && (classType = classType.getSuperclass()) != null) {
    }
    return queue;
  }

}
