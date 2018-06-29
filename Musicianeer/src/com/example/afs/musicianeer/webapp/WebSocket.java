// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.webapp;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import com.example.afs.musicianeer.message.OnWebSocketClose;
import com.example.afs.musicianeer.message.OnWebSocketConnect;
import com.example.afs.musicianeer.message.OnWebSocketText;
import com.example.afs.musicianeer.task.Message;
import com.example.afs.musicianeer.util.JsonUtilities;

public class WebSocket extends WebSocketAdapter {

  private WebApp webApp;

  public WebSocket(WebApp webApp) {
    this.webApp = webApp;
  }

  @Override
  public void onWebSocketClose(int statusCode, String reason) {
    super.onWebSocketClose(statusCode, reason);
    webApp.tsGetInputQueue().add(new OnWebSocketClose(this));
  }

  @Override
  public void onWebSocketConnect(Session sess) {
    super.onWebSocketConnect(sess);
    webApp.tsGetInputQueue().add(new OnWebSocketConnect(this));
  }

  @Override
  public void onWebSocketError(Throwable cause) {
    super.onWebSocketError(cause);
    cause.printStackTrace(System.err);
  }

  @Override
  public void onWebSocketText(String text) {
    super.onWebSocketText(text);
    webApp.tsGetInputQueue().add(new OnWebSocketText(this, text));
  }

  public void write(Message message) {
    String json = JsonUtilities.toJson(message);
    getRemote().sendStringByFuture(json);
  }
}
