// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.webapp;

import java.nio.ByteBuffer;

import com.example.afs.musicianeer.message.OnBrowserEvent;
import com.example.afs.musicianeer.message.OnBrowserEvent.Action;
import com.example.afs.musicianeer.message.OnWebSocketClose;
import com.example.afs.musicianeer.message.OnWebSocketConnect;
import com.example.afs.musicianeer.message.OnWebSocketText;
import com.example.afs.musicianeer.message.TypedMessage;
import com.example.afs.musicianeer.task.ControllerTask;
import com.example.afs.musicianeer.task.MessageBroker;
import com.example.afs.musicianeer.task.ServiceTask;
import com.example.afs.musicianeer.util.JsonUtilities;

public abstract class WebApp extends ServiceTask {

  private static final long PING_INTERVAL_MS = 5000;
  private static final ByteBuffer PING = ByteBuffer.wrap("PING".getBytes());

  private ControllerTask controllerTask;

  public WebApp(MessageBroker broker, ControllerTask controllerTask) {
    super(broker, PING_INTERVAL_MS);
    this.controllerTask = controllerTask;
    subscribe(OnWebSocketText.class, message -> doWebSocketText(message));
    subscribe(OnWebSocketClose.class, message -> doWebSocketClose(message));
    subscribe(OnWebSocketConnect.class, message -> doWebSocketConnect(message));
  }

  @Override
  public void tsStart() {
    super.tsStart();
    controllerTask.tsStart();
  }

  @Override
  public void tsTerminate() {
    controllerTask.tsTerminate();
    super.tsTerminate();
  }

  protected abstract void onPing(ByteBuffer ping);

  @Override
  protected void onTimeout() throws InterruptedException {
    onPing(PING);
  }

  protected abstract void onWebSocketClose(WebSocket webSocket);

  protected abstract void onWebSocketConnect(WebSocket webSocket);

  private void doWebSocketClose(OnWebSocketClose message) {
    onWebSocketClose(message.getWebSocket());
  }

  private void doWebSocketConnect(OnWebSocketConnect message) {
    onWebSocketConnect(message.getWebSocket());
    controllerTask.tsGetInputQueue().add(new OnBrowserEvent(Action.LOAD));
  }

  private void doWebSocketText(OnWebSocketText message) {
    String json = message.getText();
    //System.out.println("Received " + json);
    TypedMessage typedMessage = JsonUtilities.fromJson(json, TypedMessage.class);
    String messageType = typedMessage.getType();
    if (messageType == null) {
      throw new IllegalArgumentException("Missing messageType");
    }
    if (messageType.equals(OnBrowserEvent.class.getSimpleName())) {
      OnBrowserEvent onBrowserEvent = JsonUtilities.fromJson(json, OnBrowserEvent.class);
      controllerTask.tsGetInputQueue().add(onBrowserEvent);
    }
  }

}
