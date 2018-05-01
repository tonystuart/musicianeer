// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.webapp.MultitonWebApp;

public class MusicianeerWebApp extends MultitonWebApp {

  @SuppressWarnings("unused")
  private static final Logger LOG = Log.getLogger(MusicianeerWebApp.class);

  public MusicianeerWebApp(MessageBroker broker, MusicianeerWebAppFactory musicianeerWebAppFactory) {
    super(broker, musicianeerWebAppFactory, new MusicianeerController(broker));
    subscribe(OnTick.class, message -> doMessage(message));
  }

}
