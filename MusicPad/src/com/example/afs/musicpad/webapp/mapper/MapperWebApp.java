// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.mapper;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.webapp.MultitonWebApp;

public class MapperWebApp extends MultitonWebApp {

  @SuppressWarnings("unused")
  private static final Logger LOG = Log.getLogger(MapperWebApp.class);

  public MapperWebApp(MessageBroker broker, MapperWebAppFactory mapperWebAppFactory) {
    super(broker, mapperWebAppFactory, new MapperController(broker));
  }

}
