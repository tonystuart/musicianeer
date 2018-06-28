// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.main;

import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.webapp.MultitonWebApp;
import com.example.afs.musicpad.webapp.MultitonWebAppFactory;

public class MusicianeerWebAppFactory extends MultitonWebAppFactory {

  public MusicianeerWebAppFactory(MessageBroker broker) {
    super(broker);
  }

  @Override
  protected MultitonWebApp createWebApp(MessageBroker broker) {
    return new MusicianeerWebApp(broker, this);
  }
}
