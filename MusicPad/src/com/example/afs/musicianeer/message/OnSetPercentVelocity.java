// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.message;

import com.example.afs.musicianeer.task.Message;

public class OnSetPercentVelocity implements Message {

  private int percentVelocity;

  public OnSetPercentVelocity(int percentVelocity) {
    this.percentVelocity = percentVelocity;
  }

  public int getPercentVelocity() {
    return percentVelocity;
  }

  @Override
  public String toString() {
    return "OnSetPercentVelocity [percentVelocity=" + percentVelocity + "]";
  }

}
