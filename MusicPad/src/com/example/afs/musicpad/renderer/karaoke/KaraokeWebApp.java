// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.example.afs.musicpad.message.OnKaraokeBandEvent;
import com.example.afs.musicpad.message.OnKaraokeBandEvent.Action;
import com.example.afs.musicpad.message.OnKaraokeBandHtml;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.webapp.WebApp;
import com.example.afs.musicpad.webapp.WebSocket;

public class KaraokeWebApp extends WebApp {

  @SuppressWarnings("unused")
  private static final Logger LOG = Log.getLogger(KaraokeWebApp.class);

  public KaraokeWebApp(MessageBroker broker, KaraokeWebAppFactory karaokeWebAppFactory) {
    super(broker, karaokeWebAppFactory);
    setRenderer(new KaraokeRenderer(broker));
    subscribe(OnTick.class, message -> doMessage(message));
    subscribe(OnKaraokeBandHtml.class, message -> doMessage(message));
  }

  @Override
  public void onWebSocketConnection(WebSocket webSocket) {
    super.onWebSocketConnection(webSocket);
    publish(new OnKaraokeBandEvent(Action.LOAD, null));
  }

}
