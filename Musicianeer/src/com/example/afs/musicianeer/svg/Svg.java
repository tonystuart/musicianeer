// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.svg;

import com.example.afs.musicianeer.html.Parent;

public class Svg extends Parent {

  public enum Type {
    ACTUAL_SIZE, SCALE_TO_FIT
  }

  public Svg(Type type, int left, int top, int width, int height, String... properties) {
    super("svg", properties);
    switch (type) {
    case ACTUAL_SIZE:
      setProperty("width", String.valueOf(width));
      setProperty("height", String.valueOf(height));
      break;
    case SCALE_TO_FIT:
      setProperty("viewBox", left + " " + top + " " + width + " " + height);
      setProperty("preserveAspectRatio", "xMinYMin meet");
      break;
    default:
      throw new UnsupportedOperationException(type.name());
    }
  }

}