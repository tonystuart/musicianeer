// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import com.example.afs.musicpad.message.OnDeviceDetached;
import com.example.afs.musicpad.message.OnFooter;
import com.example.afs.musicpad.message.OnHeader;
import com.example.afs.musicpad.message.OnMusic;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.message.OnTransport;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;

public class WebApp extends BrokerTask<Message> {

  private static final Logger LOG = Log.getLogger(WebApp.class);
  private static final int PORT = 8080;

  private static final int CLIENTS = 10;
  private static final long PING_INTERVAL_MS = 5000;
  private static final ByteBuffer PING = ByteBuffer.wrap("PING".getBytes());

  private Server server;
  private Map<Integer, Message> indexMusic = new HashMap<>();
  private Map<Class<? extends Message>, Message> state = new HashMap<>();
  private BlockingQueue<MessageWebSocket> messageWebSockets = new LinkedBlockingQueue<>(CLIENTS);

  public WebApp(Broker<Message> broker) {
    super(broker, PING_INTERVAL_MS);
    createServer();
    subscribe(OnHeader.class, message -> doStatefulMessage(message));
    subscribe(OnFooter.class, message -> doStatefulMessage(message));
    subscribe(OnTransport.class, message -> doStatefulMessage(message));
    subscribe(OnMusic.class, message -> doMusic(message));
    subscribe(OnTick.class, message -> doMessage(message));
    subscribe(OnDeviceDetached.class, message -> doDeviceDetached(message));
  }

  public void onWebSocketConnection(MessageWebSocket messageWebSocket) {
    messageWebSockets.add(messageWebSocket);
    for (Message stateMessage : state.values()) {
      messageWebSocket.write(stateMessage);
    }
    for (Message musicMessage : indexMusic.values()) {
      messageWebSocket.write(musicMessage);
    }
  }

  public void onWebSocketText(MessageWebSocket messageWebSocket, String json) {
    System.out.println("Received " + json);
    //    Message message = JsonUtilities.fromJson(json, Message.class);
    //    if ("OnInitialize".equals(message.getType())) {
    //    }
  }

  public void removeMessageWebSocket(MessageWebSocket messageWebSocket) {
    messageWebSockets.remove(messageWebSocket);
  }

  @Override
  public void start() {
    super.start();
    try {
      server.start();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void onTimeout() throws InterruptedException {
    Iterator<MessageWebSocket> iterator = messageWebSockets.iterator();
    while (iterator.hasNext()) {
      MessageWebSocket messageWebSocket = iterator.next();
      try {
        messageWebSocket.getRemote().sendPing(PING);
      } catch (IOException e) {
        System.err.println("Client PING failed, closing WebSocket");
        messageWebSocket.getSession().close();
        iterator.remove();
      }
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

  private void doDeviceDetached(OnDeviceDetached message) {
    indexMusic.remove(message.getDeviceIndex());
    doMessage(message);
  }

  private void doMessage(Message message) {
    for (MessageWebSocket messageWebSocket : messageWebSockets) {
      messageWebSocket.write(message);
    }
  }

  private void doMusic(OnMusic message) {
    indexMusic.put(message.getDeviceIndex(), message);
    doMessage(message);
  }

  private void doStatefulMessage(Message message) {
    state.put(message.getClass(), message);
    doMessage(message);
  }

  private String getResourceBase() {
    String packageName = getClass().getPackage().getName() + ".client";
    String packageFolder = packageName.replace(".", "/");
    URL resource = getClass().getClassLoader().getResource(packageFolder);
    String resourceBase = resource.toExternalForm();
    return resourceBase;
  }

}
