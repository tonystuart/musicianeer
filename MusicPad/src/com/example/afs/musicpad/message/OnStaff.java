// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

public class OnStaff extends Message {

  private int deviceIndex;
  private String channelHtml;
  private String staffHtml;

  public OnStaff(int deviceIndex, String channelHtml, String staffHtml) {
    this.deviceIndex = deviceIndex;
    this.channelHtml = channelHtml;
    this.staffHtml = staffHtml;
  }

  public String getChannelHtml() {
    return channelHtml;
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  public String getStaffHtml() {
    return staffHtml;
  }

  @Override
  public String toString() {
    return "OnStaff [deviceIndex=" + deviceIndex + ", channelHtml=" + channelHtml + ", staffHtml=" + staffHtml + "]";
  }

}
