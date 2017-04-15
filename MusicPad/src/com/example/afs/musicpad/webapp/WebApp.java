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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;

public class WebApp extends BrokerTask<Message> {

  public static class RedirectingDefaultServlet extends DefaultServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      //      if (!request.getServerName().equals(request.getLocalAddr())) {
      //        LOG.info("requestURL=" + request.getRequestURL());
      //        String redirectLocation = "http://" + request.getLocalAddr() + ":" + PORT;
      //        response.setHeader("Location", redirectLocation);
      //        response.setStatus(Response.SC_TEMPORARY_REDIRECT);
      //      } else {
      //        super.doGet(request, response);
      //        if ("/MakingMusic.html".equals(request.getRequestURI())) {
      //        }
      //      }
      super.doGet(request, response);
    }
  }

  private static final Logger LOG = Log.getLogger(WebApp.class);
  private static final int PORT = 8080;

  private Server server;

  public WebApp(Broker<Message> broker) {
    super(broker);
    createServer();
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

  private void createServer() {
    server = new Server(PORT);
    DefaultServlet defaultServlet = new RedirectingDefaultServlet();
    ServletHolder defaultServletHolder = new ServletHolder(defaultServlet);
    defaultServletHolder.setInitParameter("resourceBase", getResourceBase());
    LOG.info("resourceBase=" + defaultServletHolder.getInitParameter("resourceBase"));
    ServletContextHandler context = new ServletContextHandler();
    context.setWelcomeFiles(new String[] {
      "MakingMusic.html"
    });
    context.addServlet(defaultServletHolder, "/");
    context.addServlet(CurrentFrameServlet.class, "/currentFrame.jpg");
    context.addServlet(RestServlet.class, "/rest/v1/*");
    HandlerCollection handlers = new HandlerCollection();
    handlers.setHandlers(new Handler[] {
        context,
        new DefaultHandler()
    });
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
