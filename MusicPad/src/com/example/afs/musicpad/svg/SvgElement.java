// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.svg;

public class SvgElement {

  public void render(StringBuilder s, int indent) {
    for (int i = 0; i < indent; i++) {
      s.append(" ");
    }
  }

  protected String format(String template, Object... parameters) {
    return String.format(template, parameters);
  }
}