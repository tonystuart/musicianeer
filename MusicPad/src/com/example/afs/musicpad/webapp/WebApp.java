// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp;

import java.nio.ByteBuffer;

import com.example.afs.musicpad.AsynchronousThread;
import com.example.afs.musicpad.message.OnBrowserEvent;
import com.example.afs.musicpad.message.OnBrowserEvent.Action;
import com.example.afs.musicpad.message.TypedMessage;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask;
import com.example.afs.musicpad.util.JsonUtilities;

public abstract class WebApp extends ServiceTask {

  private static final long PING_INTERVAL_MS = 5000;
  private static final ByteBuffer PING = ByteBuffer.wrap("PING".getBytes());

  private ControllerTask controllerTask;

  public WebApp(MessageBroker broker, ControllerTask controllerTask) {
    super(broker, PING_INTERVAL_MS);
    this.controllerTask = controllerTask;
  }

  public void onWebSocketConnection(WebSocket webSocket) {
    doWebSocketConnection(webSocket);
    controllerTask.addBrowserEvent(new OnBrowserEvent(Action.LOAD));
  }

  public void onWebSocketText(WebSocket webSocket, String json) {
    //System.out.println("Received " + json);
    TypedMessage message = JsonUtilities.fromJson(json, TypedMessage.class);
    String messageType = message.getType();
    if (messageType == null) {
      throw new IllegalArgumentException("Missing messageType");
    }
    if (messageType.equals(OnBrowserEvent.class.getSimpleName())) {
      OnBrowserEvent onBrowserEvent = JsonUtilities.fromJson(json, OnBrowserEvent.class);
      controllerTask.addBrowserEvent(onBrowserEvent);
    }
  }

  @Override
  public void start() {
    super.start();
    controllerTask.start();
  }

  @Override
  public void terminate() {
    controllerTask.terminate();
    super.terminate();
  }

  protected abstract void doPing(ByteBuffer ping);

  @AsynchronousThread
  protected abstract void doWebSocketConnection(WebSocket webSocket);

  @Override
  protected void onTimeout() throws InterruptedException {
    doPing(PING);
  }

  @AsynchronousThread
  protected abstract void onWebSocketClose(WebSocket webSocket);

}
