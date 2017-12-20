// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.karaoke;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Node;

public class Utils {

  private static final String[] COLORS = new String[] {
      "Red", // 0
      "Green", // 1
      "Blue", // 2
      "Yellow", // 3
      "Salmon", // 4
      "Goldenrod", // 5
      "Aqua", // 6
      "Orange", // 7
      "Violet", // 8
      "Brown", // 9
      "Burl", // 10
      "Silver", // 11
      "Khaki", // 12
      "Plum", // 13
      "Sienna", // 14
      "Pumpkin", // 15
  };

  public static Node createPair(String name, Object value) {
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

  public static int parseInt(String value, int defaultValue) {
    int intValue;
    try {
      intValue = Integer.parseInt(value);
    } catch (NumberFormatException e) {
      intValue = defaultValue;
    }
    return intValue;
  }

  public static Integer parseInteger(String value, int minimum, int maximum, Integer defaultIntegerValue) {
    Integer integerValue;
    try {
      int intValue = Integer.parseInt(value);
      if (intValue >= minimum && intValue <= maximum) {
        integerValue = intValue;
      } else {
        integerValue = defaultIntegerValue;
      }
    } catch (NumberFormatException e) {
      integerValue = null;
    }
    return integerValue;
  }

  public static String toMixedCase(String text) {
    boolean capitalize = true;
    StringBuilder s = new StringBuilder();
    int length = text.length();
    for (int i = 0; i < length; i++) {
      char c = text.charAt(i);
      if (capitalize) {
        c = Character.toUpperCase(c);
        capitalize = false;
      }
      if (c == '-') {
        c = ' ';
        capitalize = true;
      }
      s.append(c);
    }
    return s.toString().trim();
  }

}
