// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.task;

import com.example.afs.musicpad.message.OnBrowserEvent;
import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.webapp.WebApp;

public abstract class ControllerTask extends ServiceTask {

  private WebApp webApp;

  public ControllerTask(MessageBroker broker, long timeoutMillis) {
    super(broker, timeoutMillis);
    subscribe(OnBrowserEvent.class, (message) -> doBrowserEvent(message));
  }

  protected ControllerTask(MessageBroker broker) {
    this(broker, NO_TIMEOUT);
  }

  public void addShadowUpdate(OnShadowUpdate onShadowUpdate) {
    webApp.tsGetInputQueue().add(onShadowUpdate);
  }

  public void setWebApp(WebApp webApp) {
    this.webApp = webApp;
  }

  protected abstract void doClick(String id);

  protected abstract void doInput(String id, int value);

  protected abstract void doLoad();

  private void doBrowserEvent(OnBrowserEvent message) {
    switch (message.getAction()) {
    case LOAD:
      doLoad();
      break;
    case CLICK:
      doClick(message.getId());
      break;
    case INPUT:
      doInput(message.getId(), message.getValue());
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

}
