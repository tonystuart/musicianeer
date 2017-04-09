// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public abstract class If extends Node {

  public If(int lineIndex) {
    super(lineIndex);
  }

  @Override
  public ReturnState execute(Context context) {
    ReturnState returnState;
    if (isMatch(context)) {
      returnState = ReturnState.IF_MATCH;
      ReturnState childReturnState = executeNodes(context);
      if (childReturnState == ReturnState.IF_NO_MATCH) {
        returnState = ReturnState.IF_NO_MATCH;
      }
    } else {
      returnState = ReturnState.IF_NO_MATCH;
    }
    return returnState;
  }

  protected abstract boolean isMatch(Context context);

}