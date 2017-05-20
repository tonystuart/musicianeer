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

  private int deviceIndex;
  private String channelControls;
  private String html;

  public OnMusic(int deviceIndex, String channelControls, String html) {
    this.deviceIndex = deviceIndex;
    this.channelControls = channelControls;
    this.html = html;
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  public String getHtml() {
    return html;
  }

  @Override
  public String toString() {
    return "OnMusic [deviceIndex=" + deviceIndex + ", channelControls=" + channelControls + ", html=" + html + "]";
  }

}
