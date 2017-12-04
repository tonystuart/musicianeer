// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

public class Configuration {

  // TODO: Move this implementation to QwertyConfiguration and make this an interface
  // TODO: Don't expect input codes to be an array, request them one at a time, like legends

  protected InputMap bankMap = new InputMap("@");
  protected InputMap noteMap = new InputMap("@");

  public int[] getBankInputCodes() {
    return bankMap.getInputCodes();
  }

  public String getBankLegend(int bankIndex) {
    return bankMap.getLegends()[bankIndex] + "+";
  }

  public int[] getNoteInputCodes() {
    return noteMap.getInputCodes();
  }

  public String getNoteLegend(int noteIndex) {
    return noteMap.getLegends()[noteIndex];
  }

}
