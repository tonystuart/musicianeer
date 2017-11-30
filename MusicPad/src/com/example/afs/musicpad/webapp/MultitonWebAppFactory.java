// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp;

import com.example.afs.musicpad.task.MessageBroker;

public abstract class MultitonWebAppFactory implements WebAppFactory {
  private MessageBroker broker;

  public MultitonWebAppFactory(MessageBroker broker) {
    this.broker = broker;
  }

  @Override
  public synchronized MultitonWebApp getWebApp() {
    MultitonWebApp webApp = createWebApp(broker);
    webApp.tsStart();
    return webApp;
  }

  protected abstract MultitonWebApp createWebApp(MessageBroker broker);

}
