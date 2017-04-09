// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

import com.example.afs.musicpad.Command;

public class ThenSendHandlerCommand extends Then {

  private Command command;
  private Integer parameter;

  public ThenSendHandlerCommand(int lineIndex, String[] tokens) {
    super(lineIndex);
    try {
      command = Command.valueOf(tokens[1]);
      parameter = Integer.decode(tokens[2]);
    } catch (RuntimeException e) {
      displayError("Expected sendHandlerCommand command parameter");
    }
  }

  @Override
  public void executeThen(Context context) {
    context.getConfigurationSupport().sendHandlerCommand(command, parameter);
  }

}