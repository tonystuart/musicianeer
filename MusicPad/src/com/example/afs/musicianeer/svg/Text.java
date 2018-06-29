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

public class Text extends Node {

  private int x;
  private int y;
  private String text;

  public Text(int x, int y, String text) {
    this.x = x;
    this.y = y;
    this.text = text;
  }

  @Override
  public void render(StringBuilder s) {
    s.append(format("<text x='%d' y='%d'>%s</text>\n", x, y, text));
  }

  @Override
  public String toString() {
    return "Text [x=" + x + ", y=" + y + ", text=" + text + "]";
  }
}