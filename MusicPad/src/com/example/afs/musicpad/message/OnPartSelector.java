// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import java.util.Arrays;

public class OnPartSelector extends Message {

  private int[] deviceIndexes;
  private String html;

  public OnPartSelector(int[] deviceIndexes, String html) {
    this.deviceIndexes = deviceIndexes;
    this.html = html;
  }

  public int[] getDeviceIndexes() {
    return deviceIndexes;
  }

  public String getHtml() {
    return html;
  }

  @Override
  public String toString() {
    return "OnPartSelector [deviceIndexes=" + Arrays.toString(deviceIndexes) + ", html=" + html + "]";
  }

}
