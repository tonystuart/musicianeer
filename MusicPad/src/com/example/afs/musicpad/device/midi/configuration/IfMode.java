// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class IfMode extends If {

  private int mode;

  public IfMode(int lineIndex, String[] tokens) {
    super(lineIndex);
    try {
      mode = Integer.decode(tokens[1]);
    } catch (RuntimeException e) {
      displayError("Expected ifMode number");
    }
  }

  @Override
  protected boolean isMatch(Context context) {
    return context.getConfigurationSupport().isMode(mode);
  }

}