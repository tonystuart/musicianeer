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

    private Object requestType;
    private Rendezvous rendezvous;

    public <T extends Response> OnServiceRequested(Object requestType, Rendezvous rendezvous) {
      this.requestType = requestType;
      this.rendezvous = rendezvous;
    }

    public Rendezvous getRendezvous() {
      return rendezvous;
    }

    public Object getRequestType() {
      return requestType;
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
      try {
        queue.put(value);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

  }

  private static Map<Object, ServiceTask> globalProviders = new HashMap<>();

  public Rendezvous rendezvous = new Rendezvous();
  private Map<Object, Provider<? extends Response>> localProviders = new HashMap<>();

  public ServiceTask(MessageBroker broker, long timeoutMillis) {
    super(broker, timeoutMillis);
    subscribe(OnServiceRequested.class, (message) -> doServiceRequested(message));
  }

  protected ServiceTask(MessageBroker broker) {
    this(broker, NO_TIMEOUT);
  }

  public <T extends Response> void provide(Class<T> type, Provider<T> provider) {
    provideObject(type, provider);
  }

  public <T extends Response> void provide(String key, Provider<T> provider) {
    provideObject(key, provider);
  }

  public <T extends Response> T request(Class<T> type) {
    return requestObject(type);
  }

  public <T extends Response> T request(String key) {
    return requestObject(key);
  }

  private <T extends Response> void doServiceRequested(OnServiceRequested request) {
    @SuppressWarnings("unchecked")
    Provider<T> provider = (Provider<T>) localProviders.get(request.getRequestType());
    if (provider != null) {
      T value = provider.onRequest();
      request.getRendezvous().transfer(value);
    }
  }

  private <T extends Response> void provideObject(Object requestType, Provider<T> provider) {
    synchronized (globalProviders) {
      ServiceTask serviceTask = globalProviders.get(requestType);
      if (serviceTask != null) {
        throw new IllegalStateException("Class " + requestType + " already has provider " + serviceTask.getClass().getName());
      }
      globalProviders.put(requestType, this);
      localProviders.put(requestType, provider);
    }
  }

  private <T extends Response> T requestObject(Object requestType) {
    synchronized (globalProviders) {
      ServiceTask serviceTask = globalProviders.get(requestType);
      if (serviceTask == null) {
        throw new IllegalStateException("Class " + requestType + " does not have a provider");
      }
    }
    OnServiceRequested onServiceRequested = new OnServiceRequested(requestType, rendezvous);
    publish(onServiceRequested);
    return rendezvous.receive();
  }
}
