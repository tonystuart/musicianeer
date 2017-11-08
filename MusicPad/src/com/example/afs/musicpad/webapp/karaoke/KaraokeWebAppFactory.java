// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.karaoke;

import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.webapp.WebApp;
import com.example.afs.musicpad.webapp.WebAppFactory;

public class KaraokeWebAppFactory extends WebAppFactory {

  public KaraokeWebAppFactory(MessageBroker broker) {
    super(broker);
  }

  @Override
  protected WebApp createWebApp(MessageBroker broker, WebAppFactory webAppFactory) {
    return new KaraokeWebApp(broker, this);
  }
}
