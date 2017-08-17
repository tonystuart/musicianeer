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
import com.example.afs.musicpad.message.OnStaffPrompter;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.renderer.staff.StaffRenderer;
import com.example.afs.musicpad.util.Broker;

public class StaffWebApp extends WebApp {

  @SuppressWarnings("unused")
  private static final Logger LOG = Log.getLogger(StaffWebApp.class);

  public StaffWebApp(Broker<Message> broker, StaffWebAppFactory staffWebAppFactory) {
    super(broker, staffWebAppFactory);
    setRenderer(new StaffRenderer(broker));
    subscribe(OnTick.class, message -> doMessage(message));
    subscribe(OnStaffPrompter.class, message -> doMessage(message));
  }

}
