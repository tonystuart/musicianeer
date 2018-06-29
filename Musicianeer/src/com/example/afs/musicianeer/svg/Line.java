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

public class Line extends Node {

  private int x1;
  private int y1;
  private int x2;
  private int y2;

  public Line(int x1, int y1, int x2, int y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  @Override
  public void render(StringBuilder s) {
    s.append(format("<line x1='%d' y1='%d' x2='%d' y2='%d'/>\n", x1, y1, x2, y2));
  }

  @Override
  public String toString() {
    return "Line [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + "]";
  }
}