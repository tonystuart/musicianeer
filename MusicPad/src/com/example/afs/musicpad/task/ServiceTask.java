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

  public interface Service<T> {

  }

  private class OnServiceRequested implements Message {

    private Service<?> service;
    private Rendezvous rendezvous;

    public OnServiceRequested(Service<?> service, Rendezvous rendezvous) {
      this.service = service;
      this.rendezvous = rendezvous;
    }

    public Rendezvous getRendezvous() {
      return rendezvous;
    }

    public Service<?> getService() {
      return service;
    }

    @Override
    public String toString() {
      return "OnServiceRequested [service=" + service + "]";
    }

  }

  private class Rendezvous {

    private SynchronousQueue<Object> queue = new SynchronousQueue<>();

    @SuppressWarnings("unchecked")
    public <T> T receive() {
      try {
        return (T) queue.take();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    public <T> void transfer(T value) {
      try {
        queue.put(value);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

  }

  private static Map<Service<?>, ServiceTask> globalProviders = new HashMap<>();

  public Rendezvous rendezvous = new Rendezvous();
  private Map<Service<?>, Provider<?>> localProviders = new HashMap<>();

  public ServiceTask(MessageBroker broker, long timeoutMillis) {
    super(broker, timeoutMillis);
    subscribe(OnServiceRequested.class, (message) -> doServiceRequested(message));
  }

  protected ServiceTask(MessageBroker broker) {
    this(broker, NO_TIMEOUT);
  }

  public <T> void provide(Service<T> service, Provider<T> provider) {
    synchronized (globalProviders) {
      ServiceTask serviceTask = globalProviders.get(service);
      if (serviceTask != null) {
        throw new IllegalStateException("Class " + service + " already has provider " + serviceTask.getClass().getName());
      }
      globalProviders.put(service, this);
      localProviders.put(service, provider);
    }
  }

  public <T> T request(Service<T> service) {
    synchronized (globalProviders) {
      ServiceTask serviceTask = globalProviders.get(service);
      if (serviceTask == null) {
        throw new IllegalStateException("Class " + service + " does not have a provider");
      }
    }
    OnServiceRequested onServiceRequested = new OnServiceRequested(service, rendezvous);
    publish(onServiceRequested);
    return rendezvous.receive();
  }

  @Override
  public synchronized void tsTerminate() {
    synchronized (globalProviders) {
      for (Service<?> service : localProviders.keySet()) {
        globalProviders.remove(service, this);
      }
    }
    super.tsTerminate();
  }

  private <T> void doServiceRequested(OnServiceRequested request) {
    @SuppressWarnings("unchecked")
    Provider<T> provider = (Provider<T>) localProviders.get(request.getService());
    if (provider != null) {
      T value = provider.onRequest();
      request.getRendezvous().transfer(value);
    }
  }

}
