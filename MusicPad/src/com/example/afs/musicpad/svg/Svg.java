// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.svg;

import com.example.afs.musicpad.html.Parent;

public class Svg extends Parent {

  public Svg(int left, int top, int width, int height, String... properties) {
    super("svg", properties);
    appendProperty("viewBox", left + " " + top + " " + width + " " + height);
    appendProperty("preserveAspectRatio", "xMinYMin meet");
  }

}