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
import com.example.afs.musicpad.message.OnChannelDetails;
import com.example.afs.musicpad.message.OnChannels;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnPrompter;
import com.example.afs.musicpad.message.OnSongDetails;
import com.example.afs.musicpad.message.OnSongs;
import com.example.afs.musicpad.message.OnStaffPrompter;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.renderer.karaoke.KaraokeRenderer;
import com.example.afs.musicpad.util.Broker;

public class KaraokeWebApp extends WebApp {

  @SuppressWarnings("unused")
  private static final Logger LOG = Log.getLogger(KaraokeWebApp.class);

  public KaraokeWebApp(Broker<Message> broker, KaraokeWebAppFactory karaokeWebAppFactory) {
    super(broker, karaokeWebAppFactory);
    setRenderer(new KaraokeRenderer(broker));
    subscribe(OnTick.class, message -> doMessage(message));
    subscribe(OnCommand.class, message -> doMessage(message));
    subscribe(OnChannels.class, message -> doMessage(message));
    subscribe(OnPrompter.class, message -> doMessage(message));
    subscribe(OnStaffPrompter.class, message -> doMessage(message));
    subscribe(OnSongDetails.class, message -> doMessage(message));
    subscribe(OnChannelDetails.class, message -> doMessage(message));
    subscribe(OnSongs.class, message -> doStatefulMessage(message));
  }

}
