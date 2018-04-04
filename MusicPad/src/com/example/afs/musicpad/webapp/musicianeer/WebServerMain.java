// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.webapp.WebServer;

public class WebServerMain {
  public static void main(String[] args) {
    MessageBroker messageBroker = new MessageBroker();
    WebServer webServer = new WebServer(messageBroker);
    webServer.tsStart();
  }
}