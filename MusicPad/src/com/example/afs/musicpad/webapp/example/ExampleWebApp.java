// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.example;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.MessageTask;
import com.example.afs.musicpad.webapp.WebApp;

public class ExampleWebApp extends WebApp {

  public class ExampleRenderer extends MessageTask {
    protected ExampleRenderer(MessageBroker broker) {
      super(broker);
    }
  }

  @SuppressWarnings("unused")
  private static final Logger LOG = Log.getLogger(ExampleWebApp.class);

  public ExampleWebApp(MessageBroker broker, ExampleWebAppFactory exampleWebAppFactory) {
    super(broker, exampleWebAppFactory);
    setRenderer(new ExampleRenderer(broker));
    subscribe(OnTick.class, message -> doMessage(message));
  }

}
