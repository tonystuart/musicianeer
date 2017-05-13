// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.svg;

public class Circle extends SvgElement {

  private int cx;
  private int cy;
  private int r;
  private boolean fill;

  public Circle(int cx, int cy, int r) {
    this.cx = cx;
    this.cy = cy;
    this.r = r;
    this.fill = true;
  }

  public Circle(int cx, int cy, int r, boolean fill) {
    this.cx = cx;
    this.cy = cy;
    this.r = r;
    this.fill = fill;
  }

  @Override
  public void render(StringBuilder s, int indent) {
    super.render(s, indent);
    if (fill) {
      s.append(format("<circle cx='%d' cy='%d' r='%d'/>\n", cx, cy, r));
    } else {
      s.append(format("<circle cx='%d' cy='%d' r='%d' fill='none'/>\n", cx, cy, r));
    }
  }

  @Override
  public String toString() {
    return "Circle [cx=" + cx + ", cy=" + cy + ", r=" + r + "]";
  }
}