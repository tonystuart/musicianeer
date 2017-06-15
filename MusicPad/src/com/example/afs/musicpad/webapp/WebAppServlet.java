package com.example.afs.musicpad.webapp;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebAppServlet extends WebSocketServlet {

  private WebAppFactory webAppFactory;

  public WebAppServlet(WebAppFactory webAppFactory) {
    this.webAppFactory = webAppFactory;
  }

  @Override
  public void configure(WebSocketServletFactory factory) {
    factory.setCreator((request, response) -> new WebSocket(webAppFactory.getWebApp()));
  }
}