// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp;

import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnMusic;
import com.example.afs.musicpad.message.OnWords;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;

public class WebApp extends BrokerTask<Message> {

  private static final Logger LOG = Log.getLogger(WebApp.class);
  private static final int PORT = 8080;

  private static final int CLIENTS = 10;
  private Server server;

  private BlockingQueue<MessageWebSocket> messageWebSockets = new LinkedBlockingQueue<>(CLIENTS);

  public WebApp(Broker<Message> broker) {
    super(broker);
    createServer();
    subscribe(OnWords.class, message -> doMessage(message));
    subscribe(OnMusic.class, message -> doMessage(message));
    subscribe(OnTick.class, message -> doMessage(message));
  }

  public void addMessageWebSocket(MessageWebSocket messageWebSocket) {
    messageWebSockets.add(messageWebSocket);
  }

  public void removeMessageWebSocket(MessageWebSocket messageWebSocket) {
    messageWebSockets.remove(messageWebSocket);
  }

  @Override
  public void start() {
    super.start();
    try {
      server.start();
      server.join();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private ServletHolder createDefaultServlet() {
    DefaultServlet defaultServlet = new DefaultServlet();
    ServletHolder defaultServletHolder = new ServletHolder(defaultServlet);
    defaultServletHolder.setInitParameter("resourceBase", getResourceBase());
    LOG.info("resourceBase=" + defaultServletHolder.getInitParameter("resourceBase"));
    return defaultServletHolder;
  }

  private ServletHolder createRestServlet() {
    RestServlet restServlet = new RestServlet();
    ServletHolder restServletHolder = new ServletHolder(restServlet);
    return restServletHolder;
  }

  private void createServer() {
    ServletContextHandler context = new ServletContextHandler();
    context.setWelcomeFiles(new String[] {
      "MusicPad.html"
    });
    context.addServlet(createDefaultServlet(), "/");
    context.addServlet(CurrentFrameServlet.class, "/currentFrame.jpg");
    context.addServlet(createRestServlet(), "/v1/rest/*");
    context.addServlet(createWebSocketServlet(), "/v1/message/*");
    HandlerCollection handlers = new HandlerCollection();
    handlers.setHandlers(new Handler[] {
        context,
        new DefaultHandler()
    });
    server = new Server(PORT);
    server.setHandler(handlers);
  }

  private ServletHolder createWebSocketServlet() {
    ServletHolder webSocketServletHolder = new ServletHolder("ws-events", new MessageServlet(this));
    return webSocketServletHolder;
  }

  private void doMessage(Message message) {
    for (MessageWebSocket messageWebSocket : messageWebSockets) {
      messageWebSocket.write(message);
    }
  }

  private String getResourceBase() {
    String packageName = getClass().getPackage().getName() + ".client";
    String packageFolder = packageName.replace(".", "/");
    URL resource = getClass().getClassLoader().getResource(packageFolder);
    String resourceBase = resource.toExternalForm();
    return resourceBase;
  }

}
