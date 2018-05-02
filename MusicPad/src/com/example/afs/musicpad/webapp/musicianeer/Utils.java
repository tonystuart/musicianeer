// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

public class Utils {

  public static String capitalize(String input) {
    String output;
    output = Character.toUpperCase(input.charAt(0)) + input.substring(1);
    switch (input.length()) {
    case 0:
      output = "";
      break;
    case 1:
      output = input.toUpperCase();
      break;
    default:
      output = Character.toUpperCase(input.charAt(0)) + input.substring(1);
      break;
    }
    return output;
  }

}
