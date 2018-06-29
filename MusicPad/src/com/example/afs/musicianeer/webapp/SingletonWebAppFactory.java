// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.webapp;

import com.example.afs.musicianeer.task.MessageBroker;
import com.example.afs.musicianeer.util.DelayTimer;

public abstract class SingletonWebAppFactory implements WebAppFactory {
  private int useCount;
  private SingletonWebApp webApp;
  private MessageBroker broker;
  private DelayTimer delayTimer = new DelayTimer(() -> onTimeout());

  public SingletonWebAppFactory(MessageBroker broker) {
    this.broker = broker;
  }

  public synchronized SingletonWebApp getWebApp() {
    if (webApp == null) {
      webApp = createWebApp(broker);
      webApp.tsStart();
    }
    useCount++;
    return webApp;
  }

  public synchronized void releaseWebApp() {
    if (--useCount == 0) {
      System.out.println("WebAppFactory: scheduling WebApp termination");
      delayTimer.delay(5000);
    }
  }

  protected abstract SingletonWebApp createWebApp(MessageBroker broker);

  private synchronized void onTimeout() {
    if (useCount == 0) {
      System.out.println("WebAppFactory: terminating WebApp");
      webApp.tsTerminate();
      webApp = null;
    } else {
      System.out.println("WebAppFactory: suppressing WebApp termination");
    }
  }

}
