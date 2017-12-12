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

  public MultitonWebApp(MessageBroker broker, MultitonWebAppFactory multitonWebAppFactory, ControllerTask controllerTask) {
    super(broker, controllerTask);
    controllerTask.setWebApp(this);
    subscribe(OnShadowUpdate.class, message -> doMessage(message));
  }

  @Override
  protected void onPing(ByteBuffer ping) {
    try {
      webSocket.getRemote().sendPing(ping);
    } catch (IOException e) {
      System.err.println("Client PING failed, closing WebSocket");
      webSocket.getSession().close();
    }
  }

  @Override
  protected void onWebSocketClose(WebSocket webSocket) {
    tsTerminate();
  }

  @Override
  protected void onWebSocketConnect(WebSocket webSocket) {
    this.webSocket = webSocket;
  }

  private void doMessage(Message message) {
    if (webSocket == null) {
      throw new IllegalStateException("WebSocket is not open until doLoad is invoked, discarding message " + message);
    } else if (webSocket.isNotConnected()) {
      System.out.println("Message arrived after web socket was closed, discarding message=" + message);
    }
    webSocket.write(message);
  }

}
