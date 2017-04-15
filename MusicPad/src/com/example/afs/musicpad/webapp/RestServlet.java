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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.afs.musicpad.player.Prompter.PrompterChannel;
import com.example.afs.musicpad.util.FileUtilities;

public class RestServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  // TODO: properly decouple
  public static BlockingQueue<PrompterChannel> queue = new LinkedBlockingQueue<>();

  private Matchers matchers = new Matchers();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (matchers.isMatch("^/prompter$", request.getPathInfo())) {
      getPrompter(request, response);
    } else if (matchers.isMatch("^/instruments$", request.getPathInfo())) {
    } else if (matchers.isMatch("^/metrics$", request.getPathInfo())) {
    } else if (matchers.isMatch("^/settings$", request.getPathInfo())) {
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String[] matches;
    String pathInfo = request.getPathInfo();
    if ((matches = matchers.getMatches("/properties/([^/]+)/(.+)", pathInfo)).length > 0) {
      String name = matches[0];
      String value = matches[1];
      //Injector.getMessageBroker().publish(new PropertyChange(name, value));
    }
  }

  private void getPrompter(HttpServletRequest request, HttpServletResponse response) {
    try {
      PrompterChannel prompterChannel = queue.take();
      FileUtilities.writeJson(response.getOutputStream(), prompterChannel);
    } catch (InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }

}
