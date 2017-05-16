// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

public class OnMusic extends Message {

  private int index;
  private String html;

  public OnMusic(int index, String html) {
    this.index = index;
    this.html = html;
  }

  public String getHtml() {
    return html;
  }

  public int getIndex() {
    return index;
  }

  @Override
  public String toString() {
    return "OnMusic [index=" + index + ", html=" + html + "]";
  }

}
