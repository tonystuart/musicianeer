// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.svg;

import com.example.afs.musicpad.html.Node;

public class Ellipse extends Node {

  private int cx;
  private int cy;
  private int rx;
  private int ry;
  private String className;

  public Ellipse(int cx, int cy, int rx, int ry) {
    this.cx = cx;
    this.cy = cy;
    this.rx = rx;
    this.ry = ry;
  }

  public Ellipse(int cx, int cy, int rx, int ry, String className) {
    this.cx = cx;
    this.cy = cy;
    this.rx = rx;
    this.ry = ry;
    this.className = className;
  }

  @Override
  public void render(StringBuilder s) {
    if (className == null) {
      s.append(format("<ellipse cx='%d' cy='%d' rx='%d' ry='%d'/>\n", cx, cy, rx, ry));
    } else {
      s.append(format("<ellipse cx='%d' cy='%d' rx='%d' ry='%d' class='%s'/>\n", cx, cy, rx, ry, className));
    }
  }

  @Override
  public String toString() {
    return "Ellipse [cx=" + cx + ", cy=" + cy + ", rx=" + rx + ", ry=" + ry + ", className=" + className + "]";
  }
}