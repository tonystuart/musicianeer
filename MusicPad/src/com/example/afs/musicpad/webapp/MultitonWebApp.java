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

import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.Message;
import com.example.afs.musicpad.task.MessageBroker;

public class MultitonWebApp extends WebApp {

  private WebSocket webSocket;

  protected MultitonWebApp(MessageBroker broker, MultitonWebAppFactory multitonWebAppFactory, ControllerTask controllerTask) {
    super(broker, controllerTask);
    controllerTask.setWebApp(this);
    subscribe(OnShadowUpdate.class, message -> doMessage(message));
  }

  @Override
  public void doWebSocketConnection(WebSocket webSocket) {
    this.webSocket = webSocket;
  }

  @Override
  public void onWebSocketClose(WebSocket webSocket) {
  }

  protected void doMessage(Message message) {
    webSocket.write(message);
  }

  @Override
  protected void doPing(ByteBuffer ping) {
    try {
      webSocket.getRemote().sendPing(ping);
    } catch (IOException e) {
      System.err.println("Client PING failed, closing WebSocket");
      webSocket.getSession().close();
    }
  }

}
