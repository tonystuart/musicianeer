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

public class SendCommandRenderer {

  public static String render(Command command, Object... parameters) {
    // NB: properties are wrapped in single quotes, so use double quotes
    StringBuilder s = new StringBuilder();
    s.append("[");
    for (Object parameter : parameters) {
      if (parameter instanceof Number) {
        s.append(String.format("%d,", parameter));
      } else {
        s.append(String.format("\"%s\",", parameter));
      }
    }
    s.append("this.value");
    s.append("]");
    String result = String.format("musicPad.sendCommand(\"%s\", %s)", command.name(), s);
    return result;
  }

}
