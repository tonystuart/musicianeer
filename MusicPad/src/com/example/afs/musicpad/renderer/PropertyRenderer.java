// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;

/**
 * Formats JavaScript suitable for use in a single quoted on-property.
 */
public class PropertyRenderer {

  public static String render(Command command) {
    String javaScript = String.format("musicPad.sendCommand(\"%s\", this.value)", command.name());
    return javaScript;
  }

  public static String render(DeviceCommand deviceCommand, int deviceIndex) {
    String javaScript = String.format("musicPad.sendDeviceCommand(\"%s\", %d, this.value)", deviceCommand.name(), deviceIndex);
    return javaScript;
  }

}
