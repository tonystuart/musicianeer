// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.task;

import com.example.afs.musicianeer.message.OnBrowserEvent;
import com.example.afs.musicianeer.message.OnShadowUpdate;
import com.example.afs.musicianeer.webapp.WebApp;

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

  protected void doClick(String id) {
  }

  protected void doInput(String id, String value) {
  }

  protected void doKeyDown(String id, String keyCode) {
  }

  protected void doKeyUp(String id, String keyCode) {
  }

  protected void doLoad() {
    // Defer processing that could send shadow update messages until here
  }

  protected void doMouseDown(String id) {
  }

  protected void doMouseOut(String id) {
  }

  protected void doMouseOver(String id) {
  }

  protected void doMouseUp(String id) {
  }

  protected void doMove(String id, String value) {
  }

  protected void doScroll(String id, String value) {
  }

  protected void doSubmit(String id, String value) {
  }

  private void doBrowserEvent(OnBrowserEvent message) {
    switch (message.getAction()) {
    case CLICK:
      doClick(message.getId());
      break;
    case INPUT:
      doInput(message.getId(), message.getValue());
      break;
    case KEY_DOWN:
      doKeyDown(message.getId(), message.getValue());
      break;
    case KEY_UP:
      doKeyUp(message.getId(), message.getValue());
      break;
    case LOAD:
      doLoad();
      break;
    case MOVE:
      doMove(message.getId(), message.getValue());
      break;
    case MOUSE_DOWN:
      doMouseDown(message.getId());
      break;
    case MOUSE_OUT:
      doMouseOut(message.getId());
      break;
    case MOUSE_OVER:
      doMouseOver(message.getId());
      break;
    case MOUSE_UP:
      doMouseUp(message.getId());
      break;
    case SCROLL:
      doScroll(message.getId(), message.getValue());
      break;
    case SUBMIT:
      doSubmit(message.getId(), message.getValue());
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

}
