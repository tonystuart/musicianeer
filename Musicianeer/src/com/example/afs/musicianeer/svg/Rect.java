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

public class Rect extends Node {

  private int x;
  private int y;
  private int width;
  private int height;

  public Rect(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  @Override
  public void render(StringBuilder s) {
    s.append(format("<rect x='%d' y='%d' width='%d' height='%d'/>\n", x, y, width, height));
  }

  @Override
  public String toString() {
    return "Rect [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
  }
}