// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.svg;

import com.example.afs.musicianeer.html.Element;

// Like Line, but heavier weight... to be used only when necessary.

public class LineElement extends Element {

  public LineElement(int x1, int y1, int x2, int y2, String... properties) {
    super("line", properties);
    setSelfClosingTag(true);
    setProperty("x1", Integer.toString(x1));
    setProperty("y1", Integer.toString(y1));
    setProperty("x2", Integer.toString(x2));
    setProperty("y2", Integer.toString(y2));
  }

}