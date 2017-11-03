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
import java.util.concurrent.SynchronousQueue;

public class ServiceTask extends MessageTask {

  public interface Provider<T> {
    T onRequest();
  }

  public interface Response {

  }

  private class OnServiceRequested implements Message {

    private Class<? extends Response> type;
    private Rendezvous rendezvous;

    public <T extends Response> OnServiceRequested(Class<T> type, Rendezvous rendezvous) {
      this.type = type;
      this.rendezvous = rendezvous;
    }

    public Rendezvous getRendezvous() {
      return rendezvous;
    }

    public Class<? extends Response> getType() {
      return type;
    }

  }

  private class Rendezvous {

    private SynchronousQueue<Object> queue = new SynchronousQueue<>();

    @SuppressWarnings("unchecked")
    public <T extends Response> T receive() {
      try {
        return (T) queue.take();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    public <T extends Response> void transfer(T value) {
      queue.add(value);
    }

  }

  private static Map<Class<? extends Response>, ServiceTask> globalProviders = new HashMap<>();

  public Rendezvous rendezvous = new Rendezvous();
  private Map<Class<? extends Response>, Provider<? extends Response>> localProviders = new HashMap<>();

  protected ServiceTask(MessageBroker broker) {
    super(broker);
    subscribe(OnServiceRequested.class, (message) -> doServiceRequested(message));
  }

  public <T extends Response> void provide(Class<T> type, Provider<T> provider) {
    synchronized (globalProviders) {
      ServiceTask serviceTask = globalProviders.get(type);
      if (serviceTask != null) {
        throw new IllegalStateException("Class " + type.getName() + " already has provider " + serviceTask.getClass().getName());
      }
      localProviders.put(type, provider);
    }
  }

  public <T extends Response> T request(Class<T> type) {
    OnServiceRequested onServiceRequested = new OnServiceRequested(type, rendezvous);
    publish(onServiceRequested);
    return rendezvous.receive();

  }

  private <T extends Response> void doServiceRequested(OnServiceRequested request) {
    @SuppressWarnings("unchecked")
    Provider<T> provider = (Provider<T>) localProviders.get(request.getType());
    if (provider != null) {
      T value = provider.onRequest();
      request.getRendezvous().transfer(value);
    }
  }
}
