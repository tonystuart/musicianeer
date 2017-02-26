// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.util.MessageBroker.Message;

public class DigitPressed implements Message {

  private int deviceId;
  private int digit;

  public DigitPressed(int deviceId, int digit) {
    this.deviceId = deviceId;
    this.digit = digit;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public int getDigit() {
    return digit;
  }

  @Override
  public String toString() {
    return "DigitPressed [deviceId=" + deviceId + ", digit=" + digit + "]";
  }

}
