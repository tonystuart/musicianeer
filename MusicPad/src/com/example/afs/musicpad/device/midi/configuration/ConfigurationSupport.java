// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

import com.example.afs.musicpad.Command;

public interface ConfigurationSupport {

  void clearMode(int mode);

  boolean isMode(int mode);

  boolean isNotMode(int mode);

  void sendDeviceMessage(int port, int command, int channel, int data1, int data2);

  void sendHandlerCommand(Command command, Integer parameter);

  void sendHandlerMessage(int data1);

  void setMode(int mode);

}