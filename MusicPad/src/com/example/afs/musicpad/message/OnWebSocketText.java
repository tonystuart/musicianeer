// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.task.Message;
import com.example.afs.musicpad.webapp.WebSocket;

public class OnWebSocketText implements Message {

  private WebSocket webSocket;
  private String text;

  public OnWebSocketText(WebSocket webSocket, String text) {
    this.webSocket = webSocket;
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public WebSocket getWebSocket() {
    return webSocket;
  }

  @Override
  public String toString() {
    return "OnWebSocketText [webSocket=" + webSocket + ", text=" + text + "]";
  }

}
