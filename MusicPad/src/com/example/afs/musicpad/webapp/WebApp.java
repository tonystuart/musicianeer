// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelCommand;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnRepublishState;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.JsonUtilities;

public class WebApp extends BrokerTask<Message> {

  private static final int CLIENTS = 10;
  private static final long PING_INTERVAL_MS = 5000;
  private static final ByteBuffer PING = ByteBuffer.wrap("PING".getBytes());

  private WebAppFactory webAppFactory;
  private Map<Class<? extends Message>, Message> state = new LinkedHashMap<>();
  private BlockingQueue<WebSocket> webSockets = new LinkedBlockingQueue<>(CLIENTS);
  private BrokerTask<Message> cockpitRenderer;

  protected WebApp(Broker<Message> broker, WebAppFactory webAppFactory) {
    super(broker, PING_INTERVAL_MS);
    this.webAppFactory = webAppFactory;
  }

  public void onWebSocketConnection(WebSocket webSocket) {
    webSockets.add(webSocket);
    for (Message stateMessage : state.values()) {
      webSocket.write(stateMessage);
    }
  }

  public void onWebSocketText(WebSocket webSocket, String json) {
    System.out.println("Received " + json);
    Message message = JsonUtilities.fromJson(json, Message.class);
    if ("OnCommand".equals(message.getType())) {
      OnCommand onCommand = JsonUtilities.fromJson(json, OnCommand.class);
      getBroker().publish(onCommand);
    } else if ("OnChannelCommand".equals(message.getType())) {
      OnChannelCommand onChannelCommand = JsonUtilities.fromJson(json, OnChannelCommand.class);
      getBroker().publish(onChannelCommand);
    } else if ("OnDeviceCommand".equals(message.getType())) {
      OnDeviceCommand onDeviceCommand = JsonUtilities.fromJson(json, OnDeviceCommand.class);
      getBroker().publish(onDeviceCommand);
    }
  }

  public void removeMessageWebSocket(WebSocket webSocket) {
    webSockets.remove(webSocket);
    webAppFactory.releaseWebApp();
  }

  @Override
  public void start() {
    super.start();
    cockpitRenderer.start();
    getBroker().publish(new OnRepublishState());
  }

  @Override
  public void terminate() {
    cockpitRenderer.terminate();
    super.terminate();
  }

  protected void doMessage(Message message) {
    for (WebSocket webSocket : webSockets) {
      webSocket.write(message);
    }
  }

  protected void doStatefulMessage(Message message) {
    state.put(message.getClass(), message);
    doMessage(message);
  }

  @Override
  protected void onTimeout() throws InterruptedException {
    Iterator<WebSocket> iterator = webSockets.iterator();
    while (iterator.hasNext()) {
      WebSocket webSocket = iterator.next();
      try {
        webSocket.getRemote().sendPing(PING);
      } catch (IOException e) {
        System.err.println("Client PING failed, closing WebSocket");
        webSocket.getSession().close();
        iterator.remove();
      }
    }

  }

  protected void setRenderer(BrokerTask<Message> cockpitRenderer) {
    this.cockpitRenderer = cockpitRenderer;
  }

}
