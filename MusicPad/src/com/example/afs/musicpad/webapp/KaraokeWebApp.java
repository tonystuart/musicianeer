// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnKaraoke;
import com.example.afs.musicpad.message.OnSongSelector;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.renderer.karaoke.KaraokeRenderer;
import com.example.afs.musicpad.util.Broker;

public class KaraokeWebApp extends WebApp {

  @SuppressWarnings("unused")
  private static final Logger LOG = Log.getLogger(KaraokeWebApp.class);

  public KaraokeWebApp(Broker<Message> broker, KaraokeWebAppFactory karaokeWebAppFactory) {
    super(broker, karaokeWebAppFactory);
    setRenderer(new KaraokeRenderer(broker));
    subscribe(OnKaraoke.class, message -> doStatefulMessage(message));
    subscribe(OnSongSelector.class, message -> doStatefulMessage(message));
    subscribe(OnTick.class, message -> doMessage(message));
  }

}
