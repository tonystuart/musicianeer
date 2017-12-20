// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.staff;

import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.webapp.SingletonWebApp;
import com.example.afs.musicpad.webapp.SingletonWebAppFactory;

public class StaffWebAppFactory extends SingletonWebAppFactory {

  public StaffWebAppFactory(MessageBroker broker) {
    super(broker);
  }

  @Override
  protected SingletonWebApp createWebApp(MessageBroker broker) {
    return new StaffWebApp(broker, this);
  }
}
