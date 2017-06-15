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

public class KaraokeWebAppFactory extends WebAppFactory {

  public KaraokeWebAppFactory(Broker<Message> broker) {
    super(broker);
  }

  @Override
  protected WebApp createWebApp(Broker<Message> broker, WebAppFactory webAppFactory) {
    return new KaraokeWebApp(broker, this);
  }
}
