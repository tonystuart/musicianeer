package com.example.afs.musicpad.webapp;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import com.example.afs.musicpad.message.OnWebSocketClose;
import com.example.afs.musicpad.message.OnWebSocketConnect;
import com.example.afs.musicpad.message.OnWebSocketText;
import com.example.afs.musicpad.task.Message;
import com.example.afs.musicpad.util.JsonUtilities;

public class WebSocket extends WebSocketAdapter {

  private WebApp webApp;

  public WebSocket(WebApp webApp) {
    this.webApp = webApp;
  }

  @Override
  public void onWebSocketClose(int statusCode, String reason) {
    super.onWebSocketClose(statusCode, reason);
    webApp.getInputQueue().add(new OnWebSocketClose(this));
  }

  @Override
  public void onWebSocketConnect(Session sess) {
    super.onWebSocketConnect(sess);
    webApp.getInputQueue().add(new OnWebSocketConnect(this));
  }

  @Override
  public void onWebSocketError(Throwable cause) {
    super.onWebSocketError(cause);
    cause.printStackTrace(System.err);
  }

  @Override
  public void onWebSocketText(String text) {
    super.onWebSocketText(text);
    webApp.getInputQueue().add(new OnWebSocketText(this, text));
  }

  public void write(Message message) {
    String json = JsonUtilities.toJson(message);
    getRemote().sendStringByFuture(json);
  }
}