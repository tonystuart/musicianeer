// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

import java.util.HashMap;
import java.util.Map;

public class MidiConfiguration {

  public static final String INPUT = "input";
  public static final String INITIALIZATION = "initialization";
  public static final String CHANNEL_STATUS = "channelStatus";
  public static final String COMMAND = "command";

  private Context context = new Context();
  private Map<String, On> onBlocks = new HashMap<>();

  public Context getContext() {
    return context;
  }

  public On getOn(String key) {
    return onBlocks.get(key);
  }

  public void put(String key, On onBlock) {
    onBlocks.put(key, onBlock);
  }
}
