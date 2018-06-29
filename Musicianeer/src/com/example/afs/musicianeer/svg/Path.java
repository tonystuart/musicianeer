// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.svg;

import com.example.afs.musicianeer.html.Node;

// See https://www.w3.org/TR/SVG/paths.html
public class Path extends Node {

  private boolean isRelative;
  private String className;
  private StringBuilder data = new StringBuilder();

  public Path() {
  }

  public Path(String className) {
    this.className = className;
  }

  public Path curveTo(int cx1, int cy1, int cx2, int cy2, int x, int y) {
    data.append((isRelative ? "c" : "C") + cx1 + " " + cy1 + " " + cx2 + " " + cy2 + " " + x + " " + y + " ");
    return this;
  }

  public Path moveTo(int x, int y) {
    data.append((isRelative ? "m" : "M") + x + " " + y + " ");
    return this;
  }

  @Override
  public void render(StringBuilder s) {
    if (className == null) {
      s.append(format("<path d='%s'/>\n", data));
    } else {
      s.append(format("<path d='%s' class='%s'/>\n", data, className));
    }
  }

  public Path setRelative(boolean isRelative) {
    this.isRelative = isRelative;
    return this;
  }

}