// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import com.example.afs.musicpad.device.common.Configuration;
import com.example.afs.musicpad.task.MessageBroker;

public class MidiConfiguration extends Configuration {

  public enum ChannelState {
    SELECTED, ACTIVE, INACTIVE
  }

  private int deviceIndex;
  private MessageBroker broker;

  public MidiConfiguration(MessageBroker broker, int deviceIndex) {
    this.broker = broker;
    this.deviceIndex = deviceIndex;
  }

}
