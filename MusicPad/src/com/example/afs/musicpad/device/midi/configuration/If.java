// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class If extends Node {

  public If(int lineIndex, String[] tokens) {
    super(lineIndex, tokens);
    if (tokens.length < 2 || tokens.length > 3) {
      throw new IllegalArgumentException(formatMessage("Expected if variable or if variable value"));
    }
  }

  @Override
  public ReturnState execute(Context context) {
    ReturnState returnState;
    if (isMatch(context)) {
      if (context.isTrace()) {
        System.out.println("Match on " + this);
      }
      returnState = ReturnState.IF_MATCH;
      ReturnState childReturnState = executeNodes(context);
      if (childReturnState == ReturnState.RETURN) {
        returnState = ReturnState.RETURN;
      }
    } else {
      if (context.isTrace()) {
        System.out.println("No match on " + this);
      }
      returnState = ReturnState.IF_NO_MATCH;
    }
    return returnState;
  }

  protected boolean isMatch(Context context) {
    if (!context.isSet(tokens[1])) {
      return false;
    }
    Object left = context.getLeft(tokens[1]);
    if (tokens.length == 2) {
      return true;
    }
    if (left == null) {
      return false;
    }
    Object right = context.getRight(tokens[2]);
    if (right == null) {
      if (left instanceof Enum<?>) {
        left = ((Enum<?>) left).name();
        right = tokens[2];
      }
    }
    boolean isMatch = left.equals(right);
    return isMatch;
  }

}