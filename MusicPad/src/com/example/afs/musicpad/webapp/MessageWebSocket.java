package com.example.afs.musicpad.webapp;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.util.JsonUtilities;

public class MessageWebSocket extends WebSocketAdapter {

  private WebApp webApp;

  public MessageWebSocket(WebApp webApp) {
    this.webApp = webApp;
  }

  @Override
  public void onWebSocketClose(int statusCode, String reason) {
    super.onWebSocketClose(statusCode, reason);
    webApp.removeMessageWebSocket(this);
  }

  @Override
  public void onWebSocketConnect(Session sess) {
    super.onWebSocketConnect(sess);
    webApp.onWebSocketConnection(this);
  }

  @Override
  public void onWebSocketError(Throwable cause) {
    super.onWebSocketError(cause);
    cause.printStackTrace(System.err);
  }

  @Override
  public void onWebSocketText(String message) {
    super.onWebSocketText(message);
    webApp.onWebSocketText(this, message);
  }

  public void write(Message message) {
    String json = JsonUtilities.toJson(message);
    getRemote().sendStringByFuture(json);
  }
}