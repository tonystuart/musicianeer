// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.example.afs.musicpad.task.Broker.Subscriber;

public abstract class BrokerTask<M> extends SimpleTask<M> {

  private Broker<M> broker;
  private Map<Class<? extends M>, Subscriber<? extends M>> subscribers = new HashMap<>();
  private Map<Class<? extends M>, Subscriber<? extends M>> subscriptions = new HashMap<>();

  protected BrokerTask(Broker<M> broker) {
    this(broker, 0);
  }

  protected BrokerTask(Broker<M> broker, long timeoutMillis) {
    super(timeoutMillis);
    this.broker = broker;
  }

  @Override
  public synchronized void tsTerminate() {
    for (Entry<Class<? extends M>, Subscriber<? extends M>> subscription : subscriptions.entrySet()) {
      broker.unsubscribe(subscription.getKey(), subscription.getValue());
    }
    super.tsTerminate();
  }

  protected <T extends M> void delegate(Class<T> type, Subscriber<T> subscriber) {
    subscribers.put(type, subscriber);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void processMessage(M message) throws InterruptedException {
    Subscriber<M> subscriber;
    Class<?> classType = message.getClass();
    while ((subscriber = (Subscriber<M>) subscribers.get(classType)) == null && (classType = classType.getSuperclass()) != null) {
    }
    if (subscriber != null) {
      // NB: can be null when message placed directly on queue
      subscriber.onMessage(message);
    }
  }

  protected void publish(M message) {
    broker.publish(message);
  }

  protected void publishWithMetrics(M message) {
    long beginNanos = System.nanoTime();
    publish(message);
    long endNanos = System.nanoTime();
    double elapsedMillis = (endNanos - beginNanos) / 1000000D;
    System.out.println("publish(" + message.getClass().getSimpleName() + ") in " + elapsedMillis + " ms");
  }

  protected <T extends M> void subscribe(Class<T> type, Subscriber<T> subscriber) {
    Subscriber<T> queueAdder = message -> tsGetInputQueue().add(message);
    subscriptions.put(type, queueAdder);
    broker.subscribe(type, queueAdder);
    delegate(type, subscriber);
  }

  protected Broker<M> tsGetBroker() {
    return broker;
  }
}
