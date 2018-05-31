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

  public interface ClassProvider<T> {
    T onRequest(Service<T> service);
  }

  public interface Provider<T> {
    T onRequest();
  }

  public interface Service<T> {

  }

  private class OnClassServiceRequested implements Message {

    private Service<?> service;
    private Rendezvous rendezvous;

    public OnClassServiceRequested(Service<?> service, Rendezvous rendezvous) {
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
      return "OnClassServiceRequested [service=" + service + "]";
    }

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
        T value = (T) queue.take();
        if (value == NULL_INDICATOR) {
          value = null;
        }
        return value;
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    public <T> void transfer(T value) {
      try {
        if (value == null) {
          queue.put(NULL_INDICATOR);
        } else {
          queue.put(value);
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

  }

  private static final Object NULL_INDICATOR = "null";

  private static Map<Service<?>, ServiceTask> globalProviders = new HashMap<>();
  private static Map<Class<?>, ServiceTask> globalClassProviders = new HashMap<>();

  public Rendezvous rendezvous = new Rendezvous();
  private Map<Service<?>, Provider<?>> localProviders = new HashMap<>();
  private Map<Class<?>, ClassProvider<?>> localClassProviders = new HashMap<>();;

  public ServiceTask(MessageBroker broker, long timeoutMillis) {
    super(broker, timeoutMillis);
    subscribe(OnServiceRequested.class, (message) -> doServiceRequested(message));
    subscribe(OnClassServiceRequested.class, (message) -> doClassServiceRequested(message));
  }

  protected ServiceTask(MessageBroker broker) {
    this(broker, NO_TIMEOUT);
  }

  public <V, T extends Service<V>> void provide(Class<T> serviceClass, ClassProvider<V> classProvider) {
    synchronized (globalClassProviders) {
      ServiceTask serviceTask = globalClassProviders.get(serviceClass);
      if (serviceTask != null) {
        throw new IllegalStateException("Class " + serviceClass + " already has provider " + serviceTask.getClass().getName());
      }
      globalClassProviders.put(serviceClass, this);
      localClassProviders.put(serviceClass, classProvider);
    }
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
      if (serviceTask != null) {
        OnServiceRequested onServiceRequested = new OnServiceRequested(service, rendezvous);
        publish(onServiceRequested);
        return rendezvous.receive();
      }
    }
    synchronized (globalClassProviders) {
      ServiceTask serviceTask = globalClassProviders.get(service.getClass());
      if (serviceTask == null) {
        throw new IllegalStateException("Class " + service + " does not have a provider");
      }
    }
    OnClassServiceRequested onClassServiceRequested = new OnClassServiceRequested(service, rendezvous);
    publish(onClassServiceRequested);
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

  @SuppressWarnings("unchecked")
  private <T> void doClassServiceRequested(OnClassServiceRequested request) {
    ClassProvider<T> classProvider = (ClassProvider<T>) localClassProviders.get(request.getService().getClass());
    if (classProvider != null) {
      T value = classProvider.onRequest((Service<T>) request.getService());
      request.getRendezvous().transfer(value);
    }
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
