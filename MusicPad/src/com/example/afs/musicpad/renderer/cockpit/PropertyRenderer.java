// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.cockpit;

import com.example.afs.musicpad.ChannelCommand;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;

/**
 * Formats JavaScript suitable for use in a single quoted on-property.
 */
public class PropertyRenderer {

  public static String render(Command command) {
    String javaScript = String.format("cockpit.sendCommand(\"%s\", this.value)", command.name());
    return javaScript;
  }

  public static String render(Command command, int value) {
    String javaScript = String.format("cockpit.sendCommand(\"%s\", %d)", command.name(), value);
    return javaScript;
  }

  public static String render(DeviceCommand deviceCommand, int deviceIndex) {
    String javaScript = String.format("cockpit.sendDeviceCommand(\"%s\", %d, this.value)", deviceCommand.name(), deviceIndex);
    return javaScript;
  }

  public static String renderChannelChecked(ChannelCommand channelCommand, int channel) {
    String javaScript = String.format("cockpit.sendChannelCommand(\"%s\", %d, this.checked ? 1 : 0)", channelCommand.name(), channel);
    return javaScript;
  }

}
