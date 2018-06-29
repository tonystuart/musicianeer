// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.svg;

import com.example.afs.musicianeer.html.Node;

public class Circle extends Node {

  private int cx;
  private int cy;
  private int r;
  private String className;

  public Circle(int cx, int cy, int r) {
    this.cx = cx;
    this.cy = cy;
    this.r = r;
  }

  public Circle(int cx, int cy, int r, String className) {
    this.cx = cx;
    this.cy = cy;
    this.r = r;
    this.className = className;
  }

  @Override
  public void render(StringBuilder s) {
    if (className == null) {
      s.append(format("<circle cx='%d' cy='%d' r='%d'/>\n", cx, cy, r));
    } else {
      s.append(format("<circle cx='%d' cy='%d' r='%d' class='%s'/>\n", cx, cy, r, className));
    }
  }

  @Override
  public String toString() {
    return "Circle [cx=" + cx + ", cy=" + cy + ", r=" + r + "]";
  }
}