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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.Message;
import com.example.afs.musicpad.task.MessageBroker;

public class SingletonWebApp extends WebApp {

  private static final int CLIENTS = 50;

  private BlockingQueue<WebSocket> webSockets = new LinkedBlockingQueue<>(CLIENTS);
  private SingletonWebAppFactory singletonWebAppFactory;

  protected SingletonWebApp(MessageBroker broker, SingletonWebAppFactory singletonWebAppFactory, ControllerTask controllerTask) {
    super(broker, controllerTask);
    this.singletonWebAppFactory = singletonWebAppFactory;
    controllerTask.setWebApp(this);
    subscribe(OnShadowUpdate.class, message -> doMessage(message));
  }

  @Override
  public void doWebSocketConnection(WebSocket webSocket) {
    webSockets.add(webSocket);
  }

  @Override
  public void onWebSocketClose(WebSocket webSocket) {
    webSockets.remove(webSocket);
    singletonWebAppFactory.releaseWebApp();
  }

  protected void doMessage(Message message) {
    for (WebSocket webSocket : webSockets) {
      webSocket.write(message);
    }
  }

  @Override
  protected void doPing(ByteBuffer ping) {
    Iterator<WebSocket> iterator = webSockets.iterator();
    while (iterator.hasNext()) {
      WebSocket webSocket = iterator.next();
      try {
        webSocket.getRemote().sendPing(ping);
      } catch (IOException e) {
        System.err.println("Client PING failed, closing WebSocket");
        webSocket.getSession().close();
        iterator.remove();
      }
    }
  }

}
