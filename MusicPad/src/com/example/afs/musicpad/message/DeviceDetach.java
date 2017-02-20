// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.util.MessageBroker.Message;

public class DeviceDetach implements Message {
  private String oldDevice;

  public DeviceDetach(String oldDevice) {
    this.oldDevice = oldDevice;
  }

  public String getOldDevice() {
    return oldDevice;
  }

  @Override
  public String toString() {
    return "DeviceDetach [oldDevice=" + oldDevice + "]";
  }

}