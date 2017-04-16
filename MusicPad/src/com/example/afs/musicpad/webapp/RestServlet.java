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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.util.FileUtilities;

public class RestServlet extends HttpServlet {

  public static class OnConnect extends Message {

  }

  private static final long serialVersionUID = 1L;
  private MessageQueue messageQueue;

  private Matchers matchers = new Matchers();

  public RestServlet(MessageQueue messageQueue) {
    this.messageQueue = messageQueue;
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String[] matches;
    String pathInfo = request.getPathInfo();
    if (matchers.isMatch("^/connect$", pathInfo)) {
      OnConnect onConnect = new OnConnect();
      FileUtilities.writeJson(response.getOutputStream(), onConnect);
    } else if ((matches = matchers.getMatches("^/poll/([0-9]+)$", pathInfo)).length > 0) {
      returnFirstMessage(response, Integer.parseInt(matches[0]));
    } else {
      System.err.println("Unsupported operation " + pathInfo);
    }
  }

  @Override
  @SuppressWarnings("unused")
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String[] matches;
    String pathInfo = request.getPathInfo();
    if ((matches = matchers.getMatches("/properties/([^/]+)/(.+)", pathInfo)).length > 0) {
      String name = matches[0];
      String value = matches[1];
      //Injector.getMessageBroker().publish(new PropertyChange(name, value));
    }
  }

  private void returnFirstMessage(HttpServletResponse response, int since) throws IOException {
    Message message = messageQueue.getMessage(since);
    FileUtilities.writeJson(response.getOutputStream(), message);
  }

}
