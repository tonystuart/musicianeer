// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

public class OnHeader extends Message {

  private String title;
  private int ticksPerPixel;
  private String html;

  public OnHeader(String title, int ticksPerPixel, String html) {
    this.title = title;
    this.ticksPerPixel = ticksPerPixel;
    this.html = html;
  }

  public String getHtml() {
    return html;
  }

  public int getTicksPerPixel() {
    return ticksPerPixel;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public String toString() {
    return "OnHeader [title=" + title + ", ticksPerPixel=" + ticksPerPixel + ", html=" + html + "]";
  }

}
