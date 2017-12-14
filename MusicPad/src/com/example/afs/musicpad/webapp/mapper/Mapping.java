// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.mapper;

import com.example.afs.musicpad.device.midi.InputMessage;
import com.example.afs.musicpad.device.midi.OutputMessage;

public class Mapping {
  private InputMessage inputMessage;
  private OutputMessage outputMessage;

  public Mapping(InputMessage inputMessage, OutputMessage outputMessage) {
    this.inputMessage = inputMessage;
    this.outputMessage = outputMessage;
  }

  public InputMessage getInputMessage() {
    return inputMessage;
  }

  public OutputMessage getOutputMessage() {
    return outputMessage;
  }

}