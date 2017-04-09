// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public abstract class Then extends Node {

  public Then(int lineIndex) {
    super(lineIndex);
  }

  @Override
  public boolean execute(Context context) {
    executeThen(context);
    return false;
  }

  public abstract void executeThen(Context context);

}