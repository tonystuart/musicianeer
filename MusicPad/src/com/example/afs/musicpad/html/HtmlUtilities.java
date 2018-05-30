// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public class HtmlUtilities {

  public static String escape(String text) {
    StringBuilder s = null;
    int length = text.length();
    for (int i = 0; i < length; i++) {
      char c = text.charAt(i);
      switch (c) {
      case '<':
        if (s == null) {
          s = new StringBuilder();
          s.append(text.substring(0, i));
        }
        s.append("&lt;");
        break;
      case '>':
        if (s == null) {
          s = new StringBuilder();
          s.append(text.substring(0, i));
        }
        s.append("&gt;");
        break;
      default:
        if (s != null) {
          s.append(c);
        }
        break;
      }
    }
    return s == null ? text : s.toString();
  }

  public static void validate(String text) {
    int length = text.length();
    for (int i = 0; i < length; i++) {
      char c = text.charAt(i);
      if (c == '<' || c == '>') {
        throw new IllegalArgumentException(text);
      }
    }
  }

}
