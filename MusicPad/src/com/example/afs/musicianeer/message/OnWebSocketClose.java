// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.message;

import com.example.afs.musicianeer.task.Message;
import com.example.afs.musicianeer.webapp.WebSocket;

public class OnWebSocketClose implements Message {

  private WebSocket webSocket;

  public OnWebSocketClose(WebSocket webSocket) {
    this.webSocket = webSocket;
  }

  public WebSocket getWebSocket() {
    return webSocket;
  }

  @Override
  public String toString() {
    return "OnWebSocketClose [webSocket=" + webSocket + "]";
  }

}
