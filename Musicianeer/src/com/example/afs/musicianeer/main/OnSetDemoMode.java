// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.main;

import com.example.afs.musicianeer.task.Message;

public class OnSetDemoMode implements Message {

  private boolean isDemoMode;

  public OnSetDemoMode(boolean isDemoMode) {
    this.isDemoMode = isDemoMode;
  }

  public boolean isDemoMode() {
    return isDemoMode;
  }

  @Override
  public String toString() {
    return "OnSetDemoMode [isDemoMode=" + isDemoMode + "]";
  }

}
