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

import com.example.afs.musicpad.util.Broker.Subscriber;

public abstract class BrokerTask<M> extends SimpleTask<M> {

  private Broker<M> broker;
  private Map<Class<? extends M>, Subscriber<? extends M>> subscribers = new HashMap<>();

  protected BrokerTask(Broker<M> broker) {
    this(broker, 0);
  }

  protected BrokerTask(Broker<M> broker, long timeoutMillis) {
    super(timeoutMillis);
    this.broker = broker;
  }

  public <T extends M> void subscribe(Class<T> type, Subscriber<T> subscriber) {
    broker.subscribe(type, message -> getInputQueue().add(message));
    delegate(type, subscriber);
  }

  protected <T extends M> void delegate(Class<T> type, Subscriber<T> subscriber) {
    subscribers.put(type, subscriber);
  }

  protected Broker<M> getBroker() {
    return broker;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void processMessage(M message) throws InterruptedException {
    Subscriber<M> subscriber;
    Class<?> classType = message.getClass();
    while ((subscriber = (Subscriber<M>) subscribers.get(classType)) == null && (classType = classType.getSuperclass()) != null) {
    }
    subscriber.onMessage(message);
  }

  protected void publish(M message) {
    broker.publish(message);
  }

}
