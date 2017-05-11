// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.svg;

import java.util.LinkedList;
import java.util.List;

public class Svg extends SvgElement {

  private int width;
  private int height;
  private List<SvgElement> svgElements = new LinkedList<>();

  public Svg(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void add(SvgElement child) {
    svgElements.add(child);
  }

  public String render() {
    StringBuilder s = new StringBuilder();
    s.append(format("<svg viewBox='0 0 %d %d' preserveAspectRatio='xMinYMin meet'>", width, height));
    for (SvgElement svgElement : svgElements) {
      svgElement.render(s, 2);
    }
    s.append(format("</svg>"));
    return s.toString();
  }

}