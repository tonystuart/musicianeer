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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.MessageTask;
import com.example.afs.musicpad.webapp.example.ExampleWebAppFactory;
import com.example.afs.musicpad.webapp.karaoke.KaraokeWebAppFactory;

public class WebServer extends MessageTask {

  private static final Logger LOG = Log.getLogger(WebServer.class);
  private static final int PORT = 8080;

  private Server server;
  private ExampleWebAppFactory exampleWebAppFactory;
  private KaraokeWebAppFactory karaokeWebAppFactory;

  public WebServer(MessageBroker broker) {
    super(broker);
    karaokeWebAppFactory = new KaraokeWebAppFactory(getBroker());
    exampleWebAppFactory = new ExampleWebAppFactory(getBroker());
    createServer();
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

  private ServletHolder createDefaultServlet() {
    DefaultServlet defaultServlet = new DefaultServlet();
    ServletHolder defaultServletHolder = new ServletHolder(defaultServlet);
    defaultServletHolder.setInitParameter("resourceBase", getResourceBase());
    LOG.info("resourceBase=" + defaultServletHolder.getInitParameter("resourceBase"));
    return defaultServletHolder;
  }

  private ServletHolder createExampleServlet() {
    WebAppServlet exampleServlet = new WebAppServlet(exampleWebAppFactory);
    ServletHolder servletHolder = new ServletHolder("ExampleServlet", exampleServlet);
    return servletHolder;
  }

  private ServletHolder createKaraokeServlet() {
    WebAppServlet karaokeServlet = new WebAppServlet(karaokeWebAppFactory);
    ServletHolder servletHolder = new ServletHolder("KaraokeServlet", karaokeServlet);
    return servletHolder;
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
    context.addServlet(createExampleServlet(), "/v1/example/*");
    context.addServlet(createKaraokeServlet(), "/v1/karaoke/*");
    HandlerCollection handlers = new HandlerCollection();
    handlers.setHandlers(new Handler[] {
        context,
        new DefaultHandler()
    });
    server = new Server(PORT);
    server.setHandler(handlers);
  }

  private String getResourceBase() {
    String packageName = getClass().getPackage().getName() + ".client";
    String packageFolder = packageName.replace(".", "/");
    URL resource = getClass().getClassLoader().getResource(packageFolder);
    String resourceBase = resource.toExternalForm();
    return resourceBase;
  }

}
