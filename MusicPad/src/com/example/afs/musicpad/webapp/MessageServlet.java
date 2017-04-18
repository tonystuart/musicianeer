package com.example.afs.musicpad.webapp;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class MessageServlet extends WebSocketServlet {

  public class MessageWebSocketCreator implements WebSocketCreator {

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
      return new MessageWebSocket(webApp);
    }

  }

  private WebApp webApp;

  public MessageServlet(WebApp webApp) {
    this.webApp = webApp;
  }

  @Override
  public void configure(WebSocketServletFactory factory) {
    factory.setCreator(new MessageWebSocketCreator());
  }
}