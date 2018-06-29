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

public class OnSetPercentTempo implements Message {

  private int percentTempo;

  public OnSetPercentTempo(int percentTempo) {
    this.percentTempo = percentTempo;
  }

  public int getPercentTempo() {
    return percentTempo;
  }

  @Override
  public String toString() {
    return "OnSetPercentTempo [percentTempo=" + percentTempo + "]";
  }

}
