// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;

public class Utils {

  private static final String[] COLORS = new String[] {
      "Red",
      "Green",
      "Blue",
      "Yellow",
      "Salmon",
      "Chartreuse",
      "Aqua",
      "Orange"
  };

  public static Element createPair(String name, Object value) {
    Division division = new Division(".detail");
    division.appendChild(new Division(".name", name));
    division.appendChild(new Division(".value", value.toString()));
    return division;
  }

  public static String getPlayerName(int deviceIndex) {
    String name;
    if (deviceIndex < COLORS.length) {
      name = COLORS[deviceIndex] + " Player";
    } else {
      name = "Player " + deviceIndex;
    }
    return name;
  }

  public static String normalizeProgramName(String programName) {
    boolean ignore = false;
    boolean capitalize = true;
    StringBuilder s = new StringBuilder();
    int length = programName.length();
    for (int i = 0; i < length; i++) {
      char c = programName.charAt(i);
      if (capitalize) {
        c = Character.toUpperCase(c);
        capitalize = false;
      }
      if (c == '-') {
        c = ' ';
        capitalize = true;
      } else if (c == '(') {
        ignore = true;
      }
      if (!ignore) {
        s.append(c);
      }
      if (c == ')') {
        ignore = false;
      }
    }
    return s.toString().trim();
  }

}
