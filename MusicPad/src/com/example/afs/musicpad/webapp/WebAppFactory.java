// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.DelayTimer;

public abstract class WebAppFactory {
  private int useCount;
  private WebApp webApp;
  private Broker<Message> broker;
  private DelayTimer delayTimer = new DelayTimer(() -> onTimeout());

  public WebAppFactory(Broker<Message> broker) {
    this.broker = broker;
  }

  public synchronized WebApp getWebApp() {
    if (webApp == null) {
      webApp = createWebApp(broker, this);
      webApp.start();
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

  protected abstract WebApp createWebApp(Broker<Message> broker, WebAppFactory webAppFactory);

  private synchronized void onTimeout() {
    if (useCount == 0) {
      System.out.println("WebAppFactory: terminating WebApp");
      webApp.terminate();
      webApp = null;
    } else {
      System.out.println("WebAppFactory: suppressing WebApp termination");
    }
  }

}