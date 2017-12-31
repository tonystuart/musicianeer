// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

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
